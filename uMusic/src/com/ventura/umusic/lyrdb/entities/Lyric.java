package com.ventura.umusic.lyrdb.entities;

public class Lyric {
	private String id;
	private String musicName;
	private String artistName;
	private String lyric;

	public Lyric() {
	}

	public Lyric(String id, String musicName, String artistName) {
		this.id = id;
		this.musicName = musicName;
		this.artistName = artistName;
	}

	public Lyric(String id, String musicName, String artistName, String lyric) {
		this.id = id;
		this.musicName = musicName;
		this.artistName = artistName;
		this.lyric = lyric;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getLyric() {
		return lyric;
	}

	public void setLyric(String lyric) {
		this.lyric = lyric;
	}
	
	@Override
	public String toString() {
		return this.artistName + "'s " + this.musicName + " lyrics";
	}
}
