package com.ventura.lyricsfinder.discogs.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Artist {
	public static final String KEY_ID = "id";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_PROFILE = "profile";
	public static final String KEY_IMAGES = "images";
	public static final String KEY_NAME = "name";
	public static final String KEY_REAL_NAME = "realname";
	public static final String KEY_NAME_VARIATIONS = "namevariations";
	public static final String KEY_RELEASES_URL = "releases_url";
	public static final String KEY_URI = "uri";
	public static final String KEY_EXTERNAL_URLS = "urls";
	public static final String KEY_PROFILE_URL = "resource_url";
	public static final String KEY_DATA_QUALITY = "data_quality";
	public static final String KEY_GROUPS = "groups";
	public static final String KEY_MEMBERS = "members";

	@SerializedName(KEY_ID)
	private int id;
	@SerializedName(KEY_ACTIVE)
	private boolean isActive;
	@SerializedName(KEY_NAME)
	private String name;
	@SerializedName(KEY_REAL_NAME)
	private String realName;
	@SerializedName(KEY_NAME_VARIATIONS)
	private List<String> nameVariations = new ArrayList<String>();
	@SerializedName(KEY_PROFILE)
	private String profile;
	@SerializedName(KEY_RELEASES_URL)
	private URL releasesUrl;
	@SerializedName(KEY_URI)
	private URL discogsUrl;
	@SerializedName(KEY_EXTERNAL_URLS)
	private List<URL> externalUrlsStrings = new ArrayList<URL>();
	private List<ExternalUrl> externalUrls = new ArrayList<ExternalUrl>();
	@SerializedName(KEY_IMAGES)
	private List<Image> images = new ArrayList<Image>();
	@SerializedName(KEY_PROFILE_URL)
	private URL profileUrl;
	@SerializedName(KEY_GROUPS)
	private List<Group> groups = new ArrayList<Group>();
	@SerializedName(KEY_MEMBERS)
	private List<Artist> members = new ArrayList<Artist>();
	@SerializedName(KEY_DATA_QUALITY)
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

	public Artist() {
	}
	
	public void fillExternalUrlsList(){
		this.externalUrls.clear();
		for (int i = 0; i < externalUrlsStrings.size(); i++) {
			this.externalUrls.add(new ExternalUrl(externalUrlsStrings.get(i), this));
		}
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
		this.externalUrlsStrings.clear();
		for (int i = 0; i < externalUrls.size(); i++) {
			this.externalUrlsStrings.add(externalUrls.get(i).getExternalUrl());
		}
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
}
