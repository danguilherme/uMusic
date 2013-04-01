package com.ventura.musicexplorer.entity.music;

import java.io.Serializable;

import com.ventura.musicexplorer.entity.artist.Artist;

import android.net.Uri;

public class Track implements Serializable {
	private static final long serialVersionUID = 6928769397990780287L;

	private int id;
	private String title;
	private Uri pathUri;
	private String mimeType;

	private String duration;
	private String position;

	private Artist artist;
	private Lyrics lyrics;

	public Track(int id, String title, Uri path, String mimeType) {
		this.id = id;
		this.title = title;
		this.pathUri = path;
		this.setMimeType(mimeType);
	}

	public Track(String title, String position, String duration) {
		this.title = title;
		this.position = position;
		this.duration = duration;
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

	public Uri getPathUri() {
		return pathUri;
	}

	public void setPathUri(Uri pathUri) {
		this.pathUri = pathUri;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
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

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}

	public Lyrics getLyrics() {
		return lyrics;
	}

	public void setLyrics(Lyrics lyrics) {
		this.lyrics = lyrics;
		if (lyrics != null) {
			lyrics.setMusicName(this.getTitle());
			if (this.getArtist() != null) {
				lyrics.setArtistName(this.getArtist().getName());
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Track)) {
			return super.equals(o);
		}
		Track anotherSong = (Track) o;
		return this.id == anotherSong.id
				&& this.getTitle() == anotherSong.getTitle();
	}

	@Override
	public String toString() {
		return String.format("%1$s. %2$s (%3$s)", this.position, this.title,
				this.duration);
	}
}
