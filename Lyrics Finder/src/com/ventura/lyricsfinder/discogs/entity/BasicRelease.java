package com.ventura.lyricsfinder.discogs.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BasicRelease extends ArtistRelease {
	public static final String KEY_TRACK_LIST = "tracklist";
	public static final String KEY_STYLES = "styles";
	public static final String KEY_ARTISTS = "artists";
	public static final String KEY_COUNTRY = "country";
	public static final String KEY_GENRES = "genres";
	public static final String KEY_IMAGES = "images";

	private String country;
	private String genres;
	private String dataQuality;
	private List<Track> tracks = new ArrayList<Track>();
	private List<Image> images = new ArrayList<Image>();
	private List<String> styles = new ArrayList<String>();
	private List<Artist> artists = new ArrayList<Artist>();

	public BasicRelease(JSONObject releaseJsonObject) {
		super(releaseJsonObject);
		try {
			JSONArray arrayHelper = releaseJsonObject
					.optJSONArray(KEY_TRACK_LIST);
			for (int i = 0; arrayHelper != null && i < arrayHelper.length(); i++) {
				this.tracks.add(new Track(arrayHelper.getJSONObject(i)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}

	public String getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(String dataQuality) {
		this.dataQuality = dataQuality;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<String> getStyles() {
		return styles;
	}

	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}
}
