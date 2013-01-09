package com.ventura.lyricsfinder.discogs.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Track {

	public final String KEY_TITLE = "title";
	public final String KEY_POSITION = "position";
	public final String KEY_DURATION = "duration";

	private String title;
	private String duration;
	private int position;
	
	public Track(String title, int position, String duration) {
		this.title = title;
		this.position = position;
		this.duration = duration;
	}
	
	public Track(JSONObject jsonObjectTrack) {
		try {
			this.title = jsonObjectTrack.getString(this.KEY_TITLE);
			this.position = jsonObjectTrack.getInt(this.KEY_POSITION);
			this.duration = jsonObjectTrack.getString(this.KEY_DURATION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return this.position + ". " + this.title + " (" + this.duration + ")";
	}
}
