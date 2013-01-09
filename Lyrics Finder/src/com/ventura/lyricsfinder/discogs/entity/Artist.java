package com.ventura.lyricsfinder.discogs.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class Artist {
	public static final String KEY_ID = "id";
	public static final String KEY_ACTIVE = "active";
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
	public static final String KEY_MEMBERS = "members";

	private int id;
	private boolean isActive;
	private String name;
	private String realName;
	private List<String> nameVariations = new ArrayList<String>();
	private String profile;
	private URL releasesUrl;
	private URL discogsUrl;
	private List<ExternalUrl> externalUrls = new ArrayList<ExternalUrl>();
	private List<Image> images = new ArrayList<Image>();
	private URL profileUrl;
	private List<Group> groups = new ArrayList<Group>();
	private List<Artist> members = new ArrayList<Artist>();
	private String dataQuality;

	public Artist(int id, String name, URL discogsUrl) {
		this.id = id;
		this.name = name;
		this.discogsUrl = discogsUrl;
	}

	/**
	 * Constructor to create a band member
	 * 
	 * @param id
	 *            The id of the artist
	 * @param isActive
	 *            True if the artist is in the band, false otherwhise
	 * @param name
	 *            The name of the artist
	 * @param profileUrl
	 *            The discogs API url to get artist's individual information.
	 */
	public Artist(int id, boolean isActive, String name, URL profileUrl) {
		this.id = id;
		this.isActive = isActive;
		this.name = name;
		this.profileUrl = profileUrl;
	}

	public Artist(JSONObject config) {
		this.fill(config);
	}

	public Artist() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public List<String> getNameVariations() {
		return nameVariations;
	}

	public void setNameVariations(List<String> nameVariations) {
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

	public List<ExternalUrl> getExternalUrls() {
		return externalUrls;
	}

	public void setExternalUrls(List<ExternalUrl> externalUrls) {
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

	public List<Artist> getMembers() {
		return members;
	}

	public void setMembers(List<Artist> members) {
		this.members = members;
	}

	public String getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(String dataQuality) {
		this.dataQuality = dataQuality;
	}

	public void fill(JSONObject object) {
		try {
			// Obligatory fields
			this.id = object.getInt(Artist.KEY_ID);
			this.name = object.getString(Artist.KEY_NAME);
			this.discogsUrl = new URL(object.getString(Artist.KEY_URI));
			this.releasesUrl = new URL(
					object.getString(Artist.KEY_RELEASES_URL));
			this.profileUrl = new URL(object.getString(Artist.KEY_PROFILE_URL));
			// End of obligatory fields

			this.realName = object.optString(Artist.KEY_REALNAME);
			this.profile = object.optString(Artist.KEY_PROFILE);
			this.dataQuality = object.optString(Artist.KEY_DATA_QUALITY);

			// Loading external urls from the JSON object to an array
			JSONArray artistArraysHelper = object
					.optJSONArray(Artist.KEY_EXTERNAL_URLS);
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				this.externalUrls.add(new ExternalUrl(new URL(
						artistArraysHelper.getString(i)), this));
			}

			artistArraysHelper = object
					.optJSONArray(Artist.KEY_NAME_VARIATIONS);
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				this.nameVariations.add(artistArraysHelper.getString(i));
			}

			artistArraysHelper = object.optJSONArray(Artist.KEY_IMAGES);
			Image image;
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				JSONObject actualImage = artistArraysHelper.getJSONObject(i);
				image = new Image(
						new URL(actualImage.getString(Image.KEY_URI)),
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
			artistArraysHelper = object.optJSONArray(Artist.KEY_MEMBERS);
			for (int i = 0; artistArraysHelper != null
					&& i < artistArraysHelper.length(); i++) {
				JSONObject actualMember = artistArraysHelper.getJSONObject(i);
				Artist artist = new Artist(actualMember.getInt(Artist.KEY_ID),
						actualMember.getBoolean(Artist.KEY_ACTIVE),
						actualMember.getString(Artist.KEY_NAME), new URL(
								actualMember.getString(Artist.KEY_PROFILE_URL)));
				this.members.add(artist);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String tag = "New artist loaded";
		Log.i(tag, "Id:\t\t" + this.getId());
		Log.i(tag, "Name:\t\t" + this.getName());
		Log.i(tag, "Name Variations:\t\t" + this.getNameVariations());
		Log.i(tag, "Real Name:\t\t" + this.getRealName());
		Log.i(tag, "Discogs URL:\t\t" + this.getDiscogsUrl());
		Log.i(tag, "Profile URL:\t\t" + this.getProfileUrl());
		Log.i(tag, "Profile:\t\t" + this.getProfile());
		Log.i(tag, "External URLs:\t\t" + this.getExternalUrls().toString());
		Log.i(tag, "Groups:\t\t" + this.getGroups().toString());
		Log.i(tag, "Images:\t\t" + this.getImages().toString());
		Log.i(tag, "Releases URL:\t\t" + this.getReleasesUrl());
	}
}
