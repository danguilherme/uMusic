package com.ventura.lyricsfinder.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

public class ArtistRelease implements Comparable<ArtistRelease> {

	public static final String KEY_SEARCH_RESULT_RELEASES = "releases";

	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ROLE = "role";
	public static final String KEY_RESOURCE_URL = "resource_url";
	public static final String KEY_YEAR = "year";

	public static final String KEY_IMAGE = "thumb";
	public static final String KEY_STATUS = "status";
	public static final String KEY_MAIN_RELEASE = "main_release";
	public static final String KEY_LABEL = "label";
	public static final String KEY_TRACK_INFO = "trackinfo";
	public static final String KEY_FORMAT = "format";

	// Mandatory fields
	@SerializedName(KEY_ID)
	private int id;

	@SerializedName(KEY_TITLE)
	private String title;

	@SerializedName(KEY_TYPE)
	private String type;

	@SerializedName(KEY_ROLE)
	private String role;

	@SerializedName(KEY_RESOURCE_URL)
	private URL url;

	@SerializedName(KEY_YEAR)
	private int year;

	// Other fields
	@SerializedName(KEY_IMAGE)
	private URL thumb;
	private Image thumbImage = new Image();

	@SerializedName(KEY_STATUS)
	private String status;

	@SerializedName(KEY_MAIN_RELEASE)
	private int mainRelease;

	@SerializedName(KEY_LABEL)
	private String label;

	@SerializedName(KEY_TRACK_INFO)
	private String trackInfo;

	@SerializedName(KEY_FORMAT)
	private String format;

	private BasicRelease childRelease;

	public void loadThumb() {
		if (thumb != null) {
			thumbImage.setUrl(thumb);
		}
	}

	@Override
	public String toString() {
		return "Release " + this.title;
	}

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

	public Image getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(Image thumbImage) {
		this.thumbImage = thumbImage;
	}

	public int getMainRelease() {
		return mainRelease;
	}

	public void setMainRelease(int mainRelease) {
		this.mainRelease = mainRelease;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTrackInfo() {
		return trackInfo;
	}

	public void setTrackInfo(String trackInfo) {
		this.trackInfo = trackInfo;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public BasicRelease getChildRelease() {
		return childRelease;
	}

	public void setChildRelease(BasicRelease childRelease) {
		this.childRelease = childRelease;
	}

	public int compareTo(ArtistRelease another) {
		return this.getYear() - another.getYear();
	}
}
