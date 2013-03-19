package com.ventura.musicexplorer.discogs.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Release extends BasicRelease {
	public static final String KEY_RELEASED_FORMATTED = "released_formatted";
	public static final String KEY_MASTER_ID = "master_id";
	public static final String KEY_MASTER_URL = "master_url";
	public static final String KEY_LABELS = "labels";
	public static final String KEY_EXTRA_ARTISTS = "extraartists";
	public static final String KEY_COUNTRY = "country";

	// ???
	private int releasedFormatted;
	private int masterId;
	private URL masterUrl;

	@SerializedName(KEY_LABELS)
	private List<Label> labels = new ArrayList<Label>();
	@SerializedName(KEY_EXTRA_ARTISTS)
	private List<Artist> extraArtists = new ArrayList<Artist>();

	@SerializedName(KEY_COUNTRY)
	private String country;

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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
