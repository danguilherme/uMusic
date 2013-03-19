package com.ventura.musicexplorer.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

public class Group {
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_URI = "resource_url";
	public static final String KEY_ACTIVE = "id";

	@SerializedName(KEY_ID)
	private int id;
	@SerializedName(KEY_NAME)
	private String name;
	@SerializedName(KEY_URI)
	private URL uri;
	@SerializedName(KEY_ACTIVE)
	private boolean isActive;

	public Group(int id, String name, URL uri) {
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

	public URL getUrl() {
		return uri;
	}

	public void setUrl(URL uri) {
		this.uri = uri;
	}

	public static String getKeyActive() {
		return KEY_ACTIVE;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
