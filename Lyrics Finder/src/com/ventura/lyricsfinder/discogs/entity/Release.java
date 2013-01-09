package com.ventura.lyricsfinder.discogs.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ventura.lyricsfinder.discogs.entity.enumerator.ReleaseTypes;

public class Release implements Comparable<Release> {

	public static final String KEY_SEARCH_RESULT_RELEASES = "releases";

	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_IMAGE = "thumb";
	public static final String KEY_MAIN_RELEASE = "main_release";
	public static final String KEY_ROLE = "role";
	public static final String KEY_YEAR = "year";
	public static final String KEY_RESOURCE_URL = "resource_url";
	public static final String KEY_TYPE = "type";

	public static final String KEY_STATUS = "status";
	public static final String KEY_LABEL = "label";
	public static final String KEY_TRACK_INFO = "trackinfo";
	public static final String KEY_FORMAT = "format";

	private int id;
	private String title;
	private Image thumb;
	private int mainRelease;
	private String role;
	private int year;
	private URL url;
	private ReleaseTypes type;

	private String status;
	private String label;
	private String trackInfo;
	private String format;
	
	private List<Track> tracksList = new ArrayList<Track>();

	public Release(JSONObject releaseJsonObject) {
		try {
			this.id = releaseJsonObject.getInt(KEY_ID);
			this.title = releaseJsonObject.getString(KEY_TITLE);
			String typeString = releaseJsonObject.getString(KEY_TYPE);
			typeString = typeString.toLowerCase();
			String firstLetter = typeString.substring(0, 1).toUpperCase();
			typeString = firstLetter
					+ typeString.substring(1, typeString.length());
			this.type = Enum.valueOf(ReleaseTypes.class, typeString);
			this.url = new URL(releaseJsonObject.getString(KEY_RESOURCE_URL));
			this.role = releaseJsonObject.getString(KEY_ROLE);

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
		}
	}

	public Release() {
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

	public ReleaseTypes getType() {
		return type;
	}

	public void setType(ReleaseTypes type) {
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

	public List<Track> getTracksList() {
		return tracksList;
	}

	public void setTracksList(List<Track> tracksList) {
		this.tracksList = tracksList;
	}

	public int compareTo(Release another) {
		return this.getYear() - another.getYear();
	}

}
