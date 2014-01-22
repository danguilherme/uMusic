package com.ventura.umusic.music;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.ventura.umusic.entity.music.Audio;

/**
 * Class to handle music data info on the device.
 * 
 * @author Guilherme
 * 
 */
public class TracksManager {
	final String TAG = getClass().getName();
	private ArrayList<Audio> songsList;
	private Context context;

	// Constructor
	public TracksManager(Context ctx) {
		this.songsList = new ArrayList<Audio>();
		this.context = ctx;
	}

	/**
	 * Runs media scanner for <b>all</b> files in sd card.
	 */
	public void scanMedia() {
		scanMedia("file://" + Environment.getExternalStorageDirectory());
	}

	/**
	 * Runs media scanner to find a specific file
	 * 
	 * @param path
	 *            the file:// path
	 */
	public void scanMedia(String path) {
		Intent scanFileIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path));
		context.sendBroadcast(scanFileIntent);
	}

	/**
	 * Read all mp3 files from sdcard and returns theirs uri path
	 * 
	 * @return A list with the found audio uris.
	 */
	public List<String> getAllTracksSimplified() {
		Log.i(TAG, "START: getAllTracksSimplified");
		List<String> songPathsList = new ArrayList<String>();
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] { MediaStore.Audio.Media.DATA };

		Uri filesUri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());

		String whereSentence = String.format(
				"%1$s = \"audio/mpeg\" OR %1$s = \"audio/mp4\"",
				MediaStore.Audio.Media.MIME_TYPE);

		Cursor cursor = null;
		try {
			cursor = contentResolver.query(filesUri, columns, whereSentence,
					null, null);

			int pathColumnIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA);

			while (cursor.moveToNext()) {
				songPathsList.add(cursor.getString(pathColumnIndex));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		Log.i(TAG, "END: getAllTracksSimplified - " + songPathsList.size()
				+ " songs loaded");
		return songPathsList;
	}

	/**
	 * Gets all tracks in the device. If the tracks were already loaded, return
	 * the already loaded list.
	 * 
	 * @param force
	 *            forces the tracks to be retrieved again, even if a cached list
	 *            is already owned.
	 */
	public List<Audio> getTracks(boolean force) {
		if ((songsList != null && songsList.size() > 0) && !force)
			return songsList;
		scanMedia();
		return getAllTracks();
	}

	/**
	 * Read all mp3 files from sdcard and store the details inside a List
	 * 
	 * @return A list with the found mp3 tracks.
	 */
	public List<Audio> getAllTracks() {
		Log.i(TAG, "START: getAllTracks");
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] { MediaStore.Audio.Media.DATA };

		Uri filesUri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());

		String whereSentence = String.format(
				"%1$s = \"audio/mpeg\" OR %1$s = \"audio/mp4\"",
				MediaStore.Audio.Media.MIME_TYPE);

		Cursor cursor = null;
		try {
			cursor = contentResolver.query(filesUri, columns, whereSentence,
					null, null);

			int pathColumnIndex = cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA);

			while (cursor.moveToNext()) {
				Audio track = this.getTrackByUri(cursor
						.getString(pathColumnIndex));
				if (track != null) {
					songsList.add(track);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		Log.i(TAG, "END: getAllTracks - " + songsList.size() + " songs loaded");
		return songsList;
	}

	/**
	 * Get a track from device by its URI
	 * 
	 * @param uri
	 *            The uri of the track
	 * @return The track object, or null, if it was not found.
	 */
	public Audio getTrackByUri(String uri) {
		if (uri == null)
			return null;
		ContentResolver contentResolver = this.context.getContentResolver();

		Uri filesUri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());

		if ("content".equals(Uri.parse(uri).getScheme()))
			uri = getPath(Uri.parse(uri));

		String whereSentence = String.format("%1$s = \"%2$s\"",
				MediaStore.Audio.AudioColumns.DATA, uri);

		Audio track = null;
		Cursor cursor = null;

		try {
			cursor = contentResolver.query(filesUri,
					Constants.MUSIC_INFO_COLUMNS, whereSentence, null, null);

			// cursor.moveToFirst();
			if (cursor.moveToNext())
				track = this.loadSong(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Some providers return null if an error occurs, others throw an
			// exception
			if (cursor != null) {
				cursor.close();
			}
		}

		return track;
	}

	/**
	 * Gets the {@code file://} path of a {@code content://} one.
	 * 
	 * @param uri
	 *            The {@code content://} path uri
	 * @return
	 */
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Audio.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * Load a track by a database cursor.
	 * 
	 * The properties that will be loaded are:
	 * <ul>
	 * <li>Id</li>
	 * <li>Title</li>
	 * <li>Mime Type</li>
	 * <li>Uri</li>
	 * <li>Artist Name</li>
	 * </ul>
	 * 
	 * @param cursor
	 *            A database cursor, pointed in the line from where it will load
	 *            the track
	 * @return A {@link com.ventura.umusic.entity.music.Audio track} object
	 */
	private Audio loadSong(Cursor cursor) {
		Audio track = new Audio(cursor.getInt(Constants.MusicInfoColumns.ID
				.getIndex()), cursor.getString(Constants.MusicInfoColumns.TITLE
				.getIndex()), Uri.parse(cursor
				.getString(Constants.MusicInfoColumns.DATA.getIndex())),
				cursor.getString(Constants.MusicInfoColumns.MIME_TYPE
						.getIndex()));

		track.setArtistName(cursor.getString(Constants.MusicInfoColumns.ARTIST
				.getIndex()));
		track.setAlbumId(cursor.getLong(Constants.MusicInfoColumns.ALBUM_ID
				.getIndex()));
		track.setAlbumTitle(cursor.getString(Constants.MusicInfoColumns.ALBUM
				.getIndex()));

		return track;
	}

	/**
	 * Loads the audio inside the track's album image property
	 * 
	 * @param track
	 */
	public void loadAlbum(Audio track) {
		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri,
				track.getAlbumId());

		Log.d(TAG, albumArtUri.toString());
		Bitmap bitmap = null;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), albumArtUri);
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		track.setAlbumImage(bitmap);
	}

	private static class Constants {

		public enum MusicInfoColumns {
			ID(MediaStore.Audio.AudioColumns._ID, 0), DATA(
					MediaStore.Audio.AudioColumns.DATA, 1), TRACK(
					MediaStore.Audio.AudioColumns.TRACK, 2), TITLE(
					MediaStore.Audio.AudioColumns.TITLE, 3), DURATION(
					MediaStore.Audio.AudioColumns.DURATION, 4), MIME_TYPE(
					MediaStore.Audio.AudioColumns.MIME_TYPE, 5), ARTIST_ID(
					MediaStore.Audio.AudioColumns.ARTIST_ID, 6), ARTIST(
					MediaStore.Audio.AudioColumns.ARTIST, 7), ALBUM_ID(
					MediaStore.Audio.AudioColumns.ALBUM_ID, 8), ALBUM(
					MediaStore.Audio.AudioColumns.ALBUM, 9);

			private final String name;
			private final int index;

			MusicInfoColumns(String columnName, int columnIndex) {
				this.name = columnName;
				this.index = columnIndex;
			}

			public String getName() {
				return name;
			}

			public int getIndex() {
				return index;
			}
		}

		public static String[] MUSIC_INFO_COLUMNS = new String[] {
				MusicInfoColumns.ID.getName(), MusicInfoColumns.DATA.getName(),
				MusicInfoColumns.TRACK.getName(),
				MusicInfoColumns.TITLE.getName(),
				MusicInfoColumns.DURATION.getName(),
				MusicInfoColumns.MIME_TYPE.getName(),
				MusicInfoColumns.ARTIST_ID.getName(),
				MusicInfoColumns.ARTIST.getName(),
				MusicInfoColumns.ALBUM_ID.getName(),
				MusicInfoColumns.ALBUM.getName() };
	}
}
