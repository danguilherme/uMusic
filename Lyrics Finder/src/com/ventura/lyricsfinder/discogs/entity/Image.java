package com.ventura.lyricsfinder.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

import android.graphics.Bitmap;

public class Image {
	public static final String KEY_URI = "uri";
	public static final String KEY_HEIGHT = "height";
	public static final String KEY_WIDTH = "width";
	public static final String KEY_TYPE = "type";

	@SerializedName(KEY_URI)
	private URL url;
	
	@SerializedName(KEY_HEIGHT)
	private int height = 0;
	
	@SerializedName(KEY_WIDTH)
	private int width = 0;
	
	@SerializedName(KEY_TYPE)
	private String type;
	
	private Bitmap bitmap;

	public Image(URL uri, int width, int height, String type, Bitmap bitmap) {
		this.setUrl(uri);
		this.width = width;
		this.height = height;
		this.type = type;
		this.bitmap = bitmap;
	}
	
	public Image(URL uri, int width, int height, String type) {
		this.setUrl(uri);
		this.width = width;
		this.height = height;
		this.type = type;
	}

	public Image(URL thumbUri) {
		this.setUrl(thumbUri);
	}

	public Image() {
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
	
	public Bitmap getBitmap(){
		return this.bitmap;
	}
	
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}

	@Override
	public String toString() {
		return type + " image (" + this.url + ", " + this.width + "x"
				+ this.height + ")";
	}

}
