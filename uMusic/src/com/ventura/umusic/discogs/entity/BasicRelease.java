package com.ventura.umusic.discogs.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public abstract class BasicRelease extends ArtistRelease {
	private static final long serialVersionUID = -974581751882909240L;
	
	public static final String KEY_TRACK_LIST = "tracklist";
	public static final String KEY_STYLES = "styles";
	public static final String KEY_ARTISTS = "artists";
	public static final String KEY_GENRES = "genres";
	public static final String KEY_DATA_QUALITY = "data_quality";
	public static final String KEY_IMAGES = "images";
	
	// Cloned from server entity
	public static final String KEY_NOTES = "notes";

	@SerializedName(KEY_TRACK_LIST)
	private List<Track> tracks = new ArrayList<Track>();

	@SerializedName(KEY_IMAGES)
	private List<Image> images = new ArrayList<Image>();

	@SerializedName(KEY_STYLES)
	private List<String> styles;

	@SerializedName(KEY_GENRES)
	private List<String> genres = new ArrayList<String>();

	@SerializedName(KEY_ARTISTS)
	private List<Artist> artists = new ArrayList<Artist>();

	@SerializedName(KEY_DATA_QUALITY)
	private String dataQuality;
	
	@SerializedName(KEY_NOTES)
	private String notes;

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
