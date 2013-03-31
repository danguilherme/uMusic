package com.ventura.musicexplorer.music;

import android.net.Uri;

public class Song {

	private int id;
	private String title;
	private Uri pathUri;
	private String mimeType;

	public Song(int id, String title, Uri path, String mimeType) {
		this.id = id;
		this.title = title;
		this.pathUri = path;
		this.setMimeType(mimeType);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Uri getPathUri() {
		return pathUri;
	}

	public void setPathUri(Uri pathUri) {
		this.pathUri = pathUri;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return this.title + " (" + this.mimeType + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Song)) {
			return super.equals(o);
		}
		Song anotherSong = (Song) o;
		return this.id == anotherSong.id
				&& this.getTitle() == anotherSong.getTitle();
	}
}
