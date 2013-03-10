package com.ventura.lyricsfinder.entity.artist;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ventura.lyricsfinder.entity.ExternalUrl;
import com.ventura.lyricsfinder.entity.Image;

public class Artist implements Comparable<Artist> {
	private int id;
	private boolean isActive;
	private String name;
	private String realName;
	private List<String> aliases = new ArrayList<String>();
	private String profile;
	private URL releasesUrl;
	private URL discogsUrl;
	private List<ExternalUrl> externalUrls = new ArrayList<ExternalUrl>();
	private List<Image> images = new ArrayList<Image>();
	private URL profileUrl;
	private List<Artist> groups = new ArrayList<Artist>();
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

	public Artist() {
	}

	@Override
	public String toString() {
		return this.name;
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
		return aliases;
	}

	public void setNameVariations(List<String> nameVariations) {
		this.aliases = nameVariations;
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

	public List<Artist> getGroups() {
		return groups;
	}

	public void setGroups(List<Artist> groups) {
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

	public int compareTo(Artist another) {
		if (this.isActive == another.isActive) {
			return 0;
		} else if (this.isActive && !another.isActive) {
			return (-1);
		} else {
			return 1;
		}
	}
}
