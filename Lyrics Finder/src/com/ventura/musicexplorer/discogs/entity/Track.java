package com.ventura.musicexplorer.discogs.entity;

public class Track {

	public final String KEY_TITLE = "title";
	public final String KEY_POSITION = "position";
	public final String KEY_DURATION = "duration";

	private String title;
	private String duration;
	private String position;

	public Track(String title, String position, String duration) {
		this.title = title;
		this.position = position;
		this.duration = duration;
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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return this.position + ". " + this.title + " (" + this.duration + ")";
	}
}
