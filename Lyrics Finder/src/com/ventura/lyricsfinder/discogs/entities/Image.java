package com.ventura.lyricsfinder.discogs.entities;

import java.net.URL;

import android.net.Uri;

public class Image {
	public static final String KEY_URI = "uri";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_TYPE = "type";
	
	private int height = 0;
	private int width = 0;
	private URL url;
	private String type;
	
	public Image(URL uri, int width, int height, String type) {
		this.setUrl(uri);
		this.width = width;
		this.height = height;
		this.type = type;
	}
	
	public Image(URL thumbUri) {
		this.setUrl(thumbUri);
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

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
