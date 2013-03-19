package com.ventura.musicexplorer.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

public class Master extends BasicRelease {
	public static final String KEY_MAIN_RELEASE = "main_release";
	public static final String KEY_MAIN_RELEASE_URL = "main_release_url";
	public static final String KEY_VERSIONS_URL = "versions_url";

	@SerializedName(KEY_MAIN_RELEASE)
	private int mainRelease;

	@SerializedName(KEY_MAIN_RELEASE_URL)
	private URL mainReleaseUrl;

	@SerializedName(KEY_VERSIONS_URL)
	private URL versionsUrl;

	@Override
	public int getMainRelease() {
		return mainRelease;
	}

	@Override
	public void setMainRelease(int mainRelease) {
		this.mainRelease = mainRelease;
	}

	public URL getMainReleaseUrl() {
		return mainReleaseUrl;
	}

	public void setMainReleaseUrl(URL mainReleaseUrl) {
		this.mainReleaseUrl = mainReleaseUrl;
	}

	public URL getVersionsUrl() {
		return versionsUrl;
	}

	public void setVersionsUrl(URL versionsUrl) {
		this.versionsUrl = versionsUrl;
	}
}
