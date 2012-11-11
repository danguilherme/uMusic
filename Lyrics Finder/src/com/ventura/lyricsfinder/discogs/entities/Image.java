package com.ventura.lyricsfinder.discogs.entities;

import android.net.Uri;

public class Image {
	public static final String KEY_URI = "uri";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_TYPE = "type";
	
	private int height;
	private int width;
	private Uri uri;
	private String type;
	
	public Image(Uri uri, int width, int height, String type) {
		this.uri = uri;
		this.width = width;
		this.height = height;
		this.type = type;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
