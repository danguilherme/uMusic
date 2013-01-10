package com.ventura.lyricsfinder.discogs.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ventura.lyricsfinder.discogs.entity.enumerator.ArtistRoles;
import com.ventura.lyricsfinder.discogs.entity.enumerator.ArtistReleaseTypes;

public class ArtistRelease implements Comparable<ArtistRelease> {

	public static final String KEY_SEARCH_RESULT_RELEASES = "releases";

	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_IMAGE = "thumb";
	public static final String KEY_ROLE = "role";
	public static final String KEY_YEAR = "year";
	public static final String KEY_RESOURCE_URL = "resource_url";
	public static final String KEY_TYPE = "type";

	public static final String KEY_STATUS = "status";
	public static final String KEY_LABEL = "label";
	public static final String KEY_TRACK_INFO = "trackinfo";
	public static final String KEY_FORMAT = "format";
	public static final String KEY_MAIN_RELEASE = "main_release";

	// Mandatory fields
	private int id;
	private String title;
	private ArtistReleaseTypes type;
	private ArtistRoles role;
	private URL url;
	private int year;
	
	// Other fields
	private Image thumb;
	private String status;
	private int mainRelease;
	private String label;
	private String trackInfo;
	private String format;
	
	private BasicRelease childRelease;

	public ArtistRelease(JSONObject releaseJsonObject) {
		try {
			this.id = releaseJsonObject.getInt(KEY_ID);
			this.title = releaseJsonObject.getString(KEY_TITLE);
			String typeString = releaseJsonObject.getString(KEY_TYPE);
			typeString = typeString.toLowerCase();
			String firstLetter = typeString.substring(0, 1).toUpperCase();
			typeString = firstLetter
					+ typeString.substring(1, typeString.length());
			this.type = Enum.valueOf(ArtistReleaseTypes.class, typeString);
			this.url = new URL(releaseJsonObject.getString(KEY_RESOURCE_URL));
			this.role = Enum.valueOf(ArtistRoles.class, releaseJsonObject.getString(KEY_ROLE));

			String thumbUrl = releaseJsonObject.optString(KEY_IMAGE);
			if (!thumbUrl.equals("")) {
				this.thumb = new Image(new URL(thumbUrl));
			}
			this.mainRelease = releaseJsonObject.optInt(KEY_MAIN_RELEASE);
			this.format = releaseJsonObject.optString(KEY_FORMAT);
			this.trackInfo = releaseJsonObject.optString(KEY_TRACK_INFO);
			this.label = releaseJsonObject.optString(KEY_LABEL);
			this.year = releaseJsonObject.optInt(KEY_YEAR);
			this.status = releaseJsonObject.optString(KEY_STATUS);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public ArtistRelease() {
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

	public Image getThumb() {
		return thumb;
	}

	public void setThumb(Image thumb) {
		this.thumb = thumb;
	}

	public int getMainRelease() {
		return mainRelease;
	}

	public void setMainRelease(int mainRelease) {
		this.mainRelease = mainRelease;
	}

	public ArtistRoles getRole() {
		return role;
	}

	public void setRole(ArtistRoles role) {
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

	public ArtistReleaseTypes getType() {
		return type;
	}

	public void setType(ArtistReleaseTypes type) {
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
