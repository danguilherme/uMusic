package com.ventura.musicexplorer.entity.music;

public class Lyrics {
	private String id;
	private String artistName;
	private String songName;
	private String lyrics;

	public Lyrics(String id, String artistName, String songName, String lyrics) {
		this.id = id != null ? id : "";
		this.artistName = artistName;
		this.songName = songName;
		this.lyrics = lyrics;
	}

	public Lyrics(String artistName, String songName, String lyrics) {
		this(null, artistName, songName, lyrics);
	}

	public Lyrics(String artistName, String songName) {
		this(null, artistName, songName, null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String mArtistName) {
		this.artistName = mArtistName;
	}

	public String getMusicName() {
		return songName;
	}

	public void setMusicName(String mMusicName) {
		this.songName = mMusicName;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String mLyric) {
		this.lyrics = mLyric;
	}
}
