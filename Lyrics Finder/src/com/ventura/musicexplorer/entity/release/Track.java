package com.ventura.musicexplorer.entity.release;

import java.io.Serializable;

public class Track implements Serializable {
	private static final long serialVersionUID = -388508670290173431L;

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
		return String.format("%1$s. %2$s (%3$s)", this.position, this.title,
				this.duration);
	}
}
