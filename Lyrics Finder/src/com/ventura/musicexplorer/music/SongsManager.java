package com.ventura.musicexplorer.music;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class SongsManager {
	final String TAG = getClass().getName();
	private ArrayList<Song> songsList = new ArrayList<Song>();
	private Context context;

	// Constructor
	public SongsManager(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */
	public List<Song> refreshPlayList() {
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.MIME_TYPE };

		Uri filesUri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());
		
		StringBuilder whereSentence = new StringBuilder();
		whereSentence.append(MediaStore.Audio.Media.MIME_TYPE);
		whereSentence.append(" = \"audio/mpeg\" OR ");
		whereSentence.append(MediaStore.Audio.Media.MIME_TYPE);
		whereSentence.append(" = \"audio/mp4\"").toString();

		Cursor cursor = contentResolver.query(filesUri, columns, whereSentence.toString(),
				null, null);

		int idColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
		int titleColumnIndex = cursor
				.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int mimeTypeColumnIndex = cursor
				.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
		int dataColumnIndex = cursor
				.getColumnIndex(MediaStore.Audio.Media.DATA);

		while (cursor.moveToNext()) {
			Song song = new Song(cursor.getInt(idColumnIndex),
					cursor.getString(titleColumnIndex), Uri.parse(cursor
							.getString(dataColumnIndex)),
					cursor.getString(mimeTypeColumnIndex));

			songsList.add(song);

			Log.d(TAG, "Loaded song from device: " + song.toString());
		}
		return songsList;
	}
	
	
}
