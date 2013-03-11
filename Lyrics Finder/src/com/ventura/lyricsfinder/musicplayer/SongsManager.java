package com.ventura.lyricsfinder.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;

public class SongsManager {
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private Context context;

	// Constructor
	public SongsManager(Context ctx) {
		this.context = ctx;
	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList() {
		ContentResolver contentResolver = this.context.getContentResolver();

		String[] columns = new String[] { BaseColumns._ID, MediaColumns.TITLE,
				MediaColumns.MIME_TYPE, AudioColumns.ARTIST,
				AudioColumns.ALBUM, MediaColumns.SIZE };

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(Environment
				.getExternalStorageDirectory().getPath());
		String whereSentence = new StringBuilder()
				.append(MediaColumns.MIME_TYPE).append(" = \"audio/mpeg\" OR ")
				.append(MediaColumns.MIME_TYPE).append(" = \"audio/mp4\"")
				.toString();

		Cursor cursor = contentResolver.query(uri, columns, whereSentence,
				null, null);

		int titleIndex = cursor.getColumnIndex(MediaColumns.TITLE);
		int mimeTypeIndex = cursor.getColumnIndex(MediaColumns.MIME_TYPE);

		while (cursor.moveToNext()) {
			System.out.println(cursor.getString(titleIndex));
			System.out.println(cursor.getString(mimeTypeIndex));
		}
		/*
		 * if (home.listFiles(new FileExtensionFilter()).length > 0) { for (File
		 * file : home.listFiles()) { HashMap<String, String> song = new
		 * HashMap<String, String>(); song.put( "songTitle",
		 * file.getName().substring(0, (file.getName().length() - 4)));
		 * song.put("songPath", file.getPath());
		 * 
		 * // Adding each song to SongList songsList.add(song); } }
		 */
		// return songs list array
		return songsList;
	}

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
