package com.ventura.lyricsfinder.discogs.entity;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class Master extends BasicRelease {
	public static final String KEY_MAIN_RELEASE = "main_release";
	public static final String KEY_MAIN_RELEASE_URL = "main_release_url";
	public static final String KEY_VERSIONS_URL = "versions_url";

	private int mainRelease;
	private URL mainReleaseUrl;
	private URL versionsUrl;
	
	public Master(JSONObject masterJsonObject) {
		super(masterJsonObject);
		
		try {
			this.mainRelease = masterJsonObject.getInt(KEY_MAIN_RELEASE);
		/*} catch (MalformedURLException e) {
			e.printStackTrace();*/
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public int getMainRelease() {
		return mainRelease;
	}

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
