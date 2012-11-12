package com.ventura.lyricsfinder.discogs.entities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

public class Artist {
	public static final String KEY_ID = "id";
	public static final String KEY_PROFILE = "profile";
	public static final String KEY_IMAGES = "images";
	public static final String KEY_NAME = "name";
	public static final String KEY_REALNAME = "realname";
	public static final String KEY_NAME_VARIATIONS = "namevariations";
	public static final String KEY_RELEASES_URL = "releases_url";
	public static final String KEY_URI = "uri";
	public static final String KEY_EXTERNAL_URLS = "urls";
	public static final String KEY_PROFILE_URL = "resource_url";
	public static final String KEY_DATA_QUALITY = "data_quality";
	public static final String KEY_GROUPS = "groups";

	private int id;
	private String name;
	private String realName;
	private String nameVariations;
	private String profile;
	private URL releasesUrl;
	private URL discogsUrl;
	private List<URL> externalUrls = new ArrayList<URL>();
	private List<Image> images = new ArrayList<Image>();
	private URL profileUrl;
	private List<Group> groups = new ArrayList<Group>();
	private String dataQuality;

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

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getNameVariations() {
		return nameVariations;
	}

	public void setNameVariations(String nameVariations) {
		this.nameVariations = nameVariations;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public URL getReleasesUrl() {
		return releasesUrl;
	}

	public void setReleasesUrl(URL releasesUrl) {
		this.releasesUrl = releasesUrl;
	}

	public URL getDiscogsUrl() {
		return discogsUrl;
	}

	public void setDiscogsUrl(URL discogsUrl) {
		this.discogsUrl = discogsUrl;
	}

	public List<URL> getExternalUrls() {
		return externalUrls;
	}

	public void setExternalUrls(List<URL> externalUrls) {
		this.externalUrls = externalUrls;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public URL getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(URL profileUrl) {
		this.profileUrl = profileUrl;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(String dataQuality) {
		this.dataQuality = dataQuality;
	}

	public Artist(int id, String name, URL discogsUrl) {
		this.id = id;
		this.name = name;
		this.discogsUrl = discogsUrl;
	}

	public Artist(JSONObject config) {
		this.fill(config);
	}

	public Artist() {
	}

	public void fill(JSONObject object) {
		try {
			this.realName = object.optString(Artist.KEY_REALNAME);
			this.nameVariations = object.optString(Artist.KEY_NAME_VARIATIONS);
			this.profile = object.optString(Artist.KEY_PROFILE);
			this.dataQuality = object.optString(Artist.KEY_DATA_QUALITY);

			// Loading external urls from the JSON object to an array
			JSONArray artistArraysHelper = object
					.optJSONArray(Artist.KEY_EXTERNAL_URLS);
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				this.externalUrls.add(new URL(artistArraysHelper.getString(i)));
			}

			artistArraysHelper = object.optJSONArray(Artist.KEY_IMAGES);
			Image image;
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				JSONObject actualImage = artistArraysHelper.getJSONObject(i);
				image = new Image(new URL(actualImage
						.getString(Image.KEY_URI)),
						actualImage.getInt(Image.KEY_WIDTH),
						actualImage.getInt(Image.KEY_HEIGHT),
						actualImage.getString(Image.KEY_TYPE));
				this.images.add(image);
			}

			artistArraysHelper = object.optJSONArray(Artist.KEY_GROUPS);
			Group group;
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				JSONObject actualGroup = artistArraysHelper.getJSONObject(i);
				group = new Group(actualGroup.getInt(Group.KEY_ID),
						actualGroup.getString(Group.KEY_NAME),
						Uri.parse(actualGroup.getString(Group.KEY_URI)));
				this.groups.add(group);
			}

			// Obligatory fields
			this.id = object.getInt(Artist.KEY_ID);
			this.name = object.getString(Artist.KEY_NAME);
			this.discogsUrl = new URL(object.getString(Artist.KEY_URI));
			this.releasesUrl = new URL(
					object.getString(Artist.KEY_RELEASES_URL));
			this.profileUrl = new URL(object.getString(Artist.KEY_PROFILE_URL));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
