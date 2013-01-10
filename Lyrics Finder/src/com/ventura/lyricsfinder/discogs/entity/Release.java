package com.ventura.lyricsfinder.discogs.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Release extends BasicRelease {
	public static final String KEY_RELEASED_FORMATTED = "released_formatted";
	public static final String KEY_MASTER_ID = "master_id";
	public static final String KEY_MASTER_URL = "master_url";
	public static final String KEY_LABELS = "labels";
	public static final String KEY_EXTRA_ARTISTS = "extraartists";
	
	// ???
	private int releasedFormatted;
	private int masterId;
	private URL masterUrl;
	private List<Label> labels = new ArrayList<Label>();
	private List<Artist> extraArtists = new ArrayList<Artist>();

	public Release(JSONObject releaseJsonObject) {
		super(releaseJsonObject);
		
		try {
			this.releasedFormatted = releaseJsonObject.getInt(KEY_RELEASED_FORMATTED);
			this.masterId = releaseJsonObject.getInt(KEY_MASTER_ID);
			this.masterUrl = new URL(releaseJsonObject.getString(KEY_RELEASED_FORMATTED));
			this.labels = new ArrayList<Label>();
			
			JSONArray arrayHelper = releaseJsonObject
					.optJSONArray(KEY_LABELS);
			for (int i = 0; arrayHelper != null
					&& i < arrayHelper.length(); i++) {
				//arrayHelper.getString(i);
				this.labels.add(new Label());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public int getReleasedFormatted() {
		return releasedFormatted;
	}

	public void setReleasedFormatted(int releasedFormatted) {
		this.releasedFormatted = releasedFormatted;
	}

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

	public URL getMasterUrl() {
		return masterUrl;
	}

	public void setMasterUrl(URL masterUrl) {
		this.masterUrl = masterUrl;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public List<Artist> getExtraArtists() {
		return extraArtists;
	}

	public void setExtraArtists(List<Artist> extraArtists) {
		this.extraArtists = extraArtists;
	}
}
