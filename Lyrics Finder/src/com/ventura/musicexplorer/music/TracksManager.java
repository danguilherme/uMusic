package com.ventura.musicexplorer.music;

import java.util.ArrayList;
import java.util.List;

import com.ventura.musicexplorer.entity.artist.Artist;
import com.ventura.musicexplorer.entity.music.Track;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class TracksManager {
	final String TAG = getClass().getName();
	private ArrayList<Track> songsList = new ArrayList<Track>();
	private Context context;

	// Constructor
	public TracksManager(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */
	public List<Track> refreshPlayList() {
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.MIME_TYPE,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TRACK };

		Uri filesUri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());

		StringBuilder whereSentence = new StringBuilder();
		whereSentence.append(MediaStore.Audio.Media.MIME_TYPE);
		whereSentence.append(" = \"audio/mpeg\" OR ");
		whereSentence.append(MediaStore.Audio.Media.MIME_TYPE);
		whereSentence.append(" = \"audio/mp4\"").toString();

		Cursor cursor = contentResolver.query(filesUri, columns,
				whereSentence.toString(), null, null);

		while (cursor.moveToNext()) {
			Track track = this.loadSong(cursor);
			songsList.add(track);
			Log.d(TAG, "Loaded song from device: " + track.toString());
		}
		return songsList;
	}

	private Track loadSong(Cursor cursor) {
		int idColumnIdx = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
		int titleColumnIdx = cursor
				.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int mimeTypeColumnIdx = cursor
				.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
		int dataColumnIdx = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
		int artistColumnIdx = cursor
				.getColumnIndex(MediaStore.Audio.Media.ARTIST);

		Track song = new Track(cursor.getInt(idColumnIdx),
				cursor.getString(titleColumnIdx), Uri.parse(cursor
						.getString(dataColumnIdx)),
				cursor.getString(mimeTypeColumnIdx));

		song.setArtist(new Artist(Integer.MIN_VALUE, cursor
				.getString(artistColumnIdx)));

		return song;
	}
}
