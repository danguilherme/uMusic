package com.ventura.umusic.music;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.ventura.umusic.entity.artist.Artist;
import com.ventura.umusic.entity.music.Track;

public class TracksManager {
	final String TAG = getClass().getName();
	private ArrayList<Track> songsList = new ArrayList<Track>();
	private Context context;

	// Constructor
	public TracksManager(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Read all mp3 files from sdcard and store the details inside a List
	 * 
	 * @return A list with the found mp3 tracks.
	 */
	public List<Track> refreshPlayList() {
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
				Track track = this.getTrackByUri(cursor
						.getString(pathColumnIndex));
				if (track != null) {
					songsList.add(track);
					Log.d(TAG, "Loaded song from device: " + track.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return songsList;
	}

	/**
	 * Get a track from device by its URI
	 * 
	 * @param uri
	 *            The uri of the track
	 * @return The track object, or null, if it was not found.
	 */
	public Track getTrackByUri(String uri) {
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] {
				// Track Info
				MediaStore.Audio.AudioColumns._ID, // The id
				MediaStore.Audio.AudioColumns.DATA, // The uri
				MediaStore.Audio.AudioColumns.TRACK, // The position in the
														// album
				MediaStore.Audio.AudioColumns.TITLE, // The song title
				MediaStore.Audio.AudioColumns.DURATION, // The song duration
				MediaStore.Audio.AudioColumns.MIME_TYPE, // The file type
				// Artist Info
				MediaStore.Audio.AudioColumns.ARTIST_ID, // The album id
				MediaStore.Audio.AudioColumns.ARTIST, // The artist name
				// Album Info
				MediaStore.Audio.AudioColumns.ALBUM_ID, // The album id
				MediaStore.Audio.AudioColumns.ALBUM // The album name
		};

		Uri filesUri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());

		if ("content".equals(Uri.parse(uri).getScheme()))
			uri = getPath(Uri.parse(uri));

		String whereSentence = String.format("%1$s = \"%2$s\"",
				MediaStore.Audio.AudioColumns.DATA, uri,
				MediaStore.Audio.AudioColumns.MIME_TYPE);
		Log.d(TAG, "Searching for: " + whereSentence);

		Track track = null;
		Cursor cursor = null;

		try {
			cursor = contentResolver.query(filesUri, columns, whereSentence,
					null, null);

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

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Audio.Media.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Deprecated
	/**
	 * Gets an artist by his/her id and then use the loadArtist method to
	 * populate it.
	 * 
	 * @param id
	 * @return
	 */
	private Artist getArtistById(int id) {
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] {
				// Artist Info
				MediaStore.Audio.Artists._ID, // The id
				MediaStore.Audio.Artists.ARTIST, // The artist name
				MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, // The number of
															// albums this
															// artist has
				MediaStore.Audio.Artists.NUMBER_OF_TRACKS // The number of
															// tracks this
															// artist have
		};

		Uri filesUri = MediaStore.Audio.Artists.getContentUri(Environment
				.getExternalStorageDirectory().getName());

		String whereSentence = String.format("%1$s = \"%2$s\"",
				MediaStore.Audio.Artists._ID, id);

		Artist artist = null;
		Cursor cursor = null;

		try {
			cursor = contentResolver.query(filesUri, columns, whereSentence,
					null, null);

			if (cursor != null && cursor.moveToNext())
				artist = this.loadArtist(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Some providers return null if an error occurs, others throw an
			// exception
			if (cursor != null) {
				cursor.close();
			}
		}

		return artist;
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
	 *            The database cursor, pointed in the line from where it will
	 *            load the track
	 * @return A {@link com.ventura.umusic.entity.music.Track track} object
	 */
	private Track loadSong(Cursor cursor) {
		Track track = new Track(cursor.getInt(0), cursor.getString(3),
				Uri.parse(cursor.getString(1)), cursor.getString(5));

		track.setArtist(new Artist(cursor.getInt(6), cursor.getString(7)));

		return track;
	}

	private Artist loadArtist(Cursor cursor) {
		Artist artist = new Artist(cursor.getInt(0), cursor.getString(1));
		return artist;
	}
}
