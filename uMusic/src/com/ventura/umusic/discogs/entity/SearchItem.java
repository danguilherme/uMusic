package com.ventura.umusic.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;
import com.ventura.umusic.discogs.entity.enumerator.QueryType;

public class SearchItem {
	public static final String KEY_THUMB = "thumb";
	public static final String KEY_TITLE = "title";
	public static final String KEY_URI = "uri";
	public static final String KEY_RESOURCE_URL = "resource_url";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ID = "id";

	@SerializedName(KEY_ID)
	private int id;
	@SerializedName(KEY_TITLE)
	private String title;
	@SerializedName(KEY_THUMB)
	private URL thumbURL;
	@SerializedName(KEY_TYPE)
	private QueryType type;
	@SerializedName(KEY_URI)
	private String relativeUrl;
	@SerializedName(KEY_RESOURCE_URL)
	private URL url;

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

	public URL getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(URL thumbURL) {
		this.thumbURL = thumbURL;
	}

	public QueryType getType() {
		return type;
	}

	public void setType(QueryType type) {
		this.type = type;
	}

	public String getRelativeUrl() {
		return relativeUrl;
	}

	public void setRelativeUrl(String relativeUrl) {
		this.relativeUrl = relativeUrl;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return this.id + "/" + this.title + " - " + this.url;
	}
}
