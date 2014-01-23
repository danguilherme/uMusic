package com.ventura.umusic.entity.music;

import java.io.Serializable;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Logical representation of an audio media
 * 
 * @author Guilherme
 * 
 */
public class Audio implements Serializable {
	private static final long serialVersionUID = 6928769397990780287L;

	public static final String KEY = "com.ventura.umusic.entity.music.Audio.KEY";
	public static final String KEY_URI = "com.ventura.umusic.entity.music.Audio.KEY_URI";

	private int id;
	private String title;
	private String mimeType;
	private Uri pathUri;

	private String duration;
	private String position;

	private String artistName;
	private String albumTitle;

	private String lyrics;

	private long albumId;
	private Bitmap albumImage;

	public Audio(int id, String title, Uri path, String mimeType) {
		this.id = id;
		this.title = title;
		this.pathUri = path;
		this.setMimeType(mimeType);
	}

	public Audio(String title, String position, String duration) {
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

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getAlbumTitle() {
		return albumTitle;
	}

	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public Uri getAlbumArtUri() {
		if (getAlbumId() > 0)
			return ContentUris.withAppendedId(
					Uri.parse("content://media/external/audio/albumart"),
					getAlbumId());
		return null;
	}

	public Bitmap getAlbumImage() {
		return albumImage;
	}

	public void setAlbumImage(Bitmap album) {
		this.albumImage = album;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Audio)) {
			return super.equals(o);
		}
		Audio anotherSong = (Audio) o;
		return this.id == anotherSong.id
				&& this.getTitle().equals(anotherSong.getTitle());
	}

	@Override
	public String toString() {
		return String.format("%1$s. %2$s (%3$s)", this.position, this.title,
				this.duration);
	}
}
