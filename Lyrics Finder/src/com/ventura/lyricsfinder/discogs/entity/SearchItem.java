package com.ventura.lyricsfinder.discogs.entity;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.ventura.lyricsfinder.discogs.entity.enumerator.QueryType;

public class SearchItem {
	public static final String KEY_THUMB = "thumb";
	public static final String KEY_TITLE = "title";
	public static final String KEY_URI = "uri";
	public static final String KEY_RESOURCE_URL = "resource_url";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ID = "id";

	private QueryType type;
	private Artist artist;

	public SearchItem(QueryType type, Object searchResponse) {
		switch (type) {
		case Artist:
			this.artist = (Artist) searchResponse;
			break;
		default:
			break;
		}
	}

	public SearchItem(QueryType type, JSONObject config) {
		switch (type) {
		case Artist:
			this.artist = new Artist();
			try {
				artist.setId(config.getInt(KEY_ID));
				artist.setName(config.getString(KEY_TITLE));
				artist.setProfileUrl(new URL(config.getString(KEY_RESOURCE_URL)));
				artist.getImages().add(
						new Image(new URL(config.getString(KEY_THUMB))));
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	public QueryType getType() {
		return type;
	}

	public void setType(QueryType type) {
		this.type = type;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

}
