package com.ventura.musicexplorer.music.player;

import com.ventura.musicexplorer.entity.music.Track;

/**
 * Holds a mirror of a song object from android database, transforming it to be
 * used in playlists.
 * 
 * @author Guilherme
 * 
 */
public class Song {
	/**
	 * The _id of this song on the android database
	 */
	private long id;

	/**
	 * The position of this song in its current playlist.
	 */
	private int position;
	
	/**
	 * The path of this song in the Android device.
	 */
	private String path;

	/**
	 * The track of this song, containing all its information.
	 */
	private Track track;

	public Song(long id, String path) {
		this.id = id;
		this.path = path;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
