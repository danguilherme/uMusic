package com.ventura.umusic.entity.music;

import java.util.ArrayList;
import java.util.List;

public class Lyrics {
	private String id;
	private String artistName;
	private String songName;
	private String lyricsText;

	public Lyrics(String id, String artistName, String songName, String lyrics) {
		this.id = id != null ? id : "";
		this.artistName = artistName;
		this.songName = songName;
		this.lyricsText = lyrics;
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

	public String getLyricsText() {
		return lyricsText;
	}

	public void setLyricsText(String mLyric) {
		this.lyricsText = mLyric;
	}

	public List<String> getLyricsStrophes() {
		List<String> strophes = new ArrayList<String>();
		if (this.lyricsText == null)
			return strophes;
		
		for (String strophe : lyricsText.split("\n\n+"))
			strophes.add(strophe);
		
		return strophes;
	}
}
