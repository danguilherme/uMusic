package com.ventura.lyricsfinder.discogs.entity;

import android.net.Uri;

public class Group {
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_URI = "resource_url";

	private int id;
	private String name;
	private Uri uri;

	public Group(int id, String name, Uri uri) {
		this.id = id;
		this.name = name;
		this.uri = uri;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
