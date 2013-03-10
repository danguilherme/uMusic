package com.ventura.lyricsfinder.lyrics;

public class Lyric {
	private String mArtistName;
	private String mMusicName;
	private String mLyric;

	public Lyric() {
	}

	public Lyric(String artistName, String musicName, String lyrics) {
		this.mArtistName = artistName;
		this.mMusicName = musicName;
		this.mLyric = lyrics;
	}

	public String getArtistName() {
		return mArtistName;
	}

	public void setArtistName(String mArtistName) {
		this.mArtistName = mArtistName;
	}

	public String getMusicName() {
		return mMusicName;
	}

	public void setMusicName(String mMusicName) {
		this.mMusicName = mMusicName;
	}

	public String getLyric() {
		return mLyric;
	}

	public void setLyric(String mLyric) {
		this.mLyric = mLyric;
	}
}
