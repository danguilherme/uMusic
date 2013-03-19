package com.ventura.musicexplorer.entity.release;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ventura.musicexplorer.entity.Image;
import com.ventura.musicexplorer.entity.artist.Artist;
import com.ventura.musicexplorer.entity.enumerator.ReleaseType;

public class ArtistRelease implements Comparable<ArtistRelease>, Serializable {
	private static final long serialVersionUID = 4281097578238872681L;
	
	private int id;
	private String title;
	private ReleaseType type;
	private String role;
	private URL url;
	private int year;
	private Image thumbImage;

	private List<Image> images = new ArrayList<Image>();
	private List<String> styles = new ArrayList<String>();
	private List<String> genres = new ArrayList<String>();
	private List<Artist> artists = new ArrayList<Artist>();
	private List<Track> trackList = new ArrayList<Track>();
	private String trackInfo;

	private String dataQuality;

	private boolean isComplete = false;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ReleaseType getType() {
		return type;
	}

	public void setType(ReleaseType type) {
		this.type = type;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Image getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(Image thumbImage) {
		this.thumbImage = thumbImage;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<String> getStyles() {
		return styles;
	}

	public void setStyles(List<String> styles) {
		this.styles = styles;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	public List<Track> getTrackList() {
		return trackList;
	}

	public void setTrackList(List<Track> trackList) {
		this.trackList = trackList;
	}

	public String getTrackInfo() {
		return trackInfo;
	}

	public void setTrackInfo(String trackInfo) {
		this.trackInfo = trackInfo;
	}

	public String getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(String dataQuality) {
		this.dataQuality = dataQuality;
	}

	public int getId() {
		return id;
	}

	/**
	 * Return <code>true</code> if this release is complete, which means that it
	 * has a track list, envolved artists list, etc. In other words, if
	 * isComplete is <code>false</code>, this release is coming from a search,
	 * otherwise, if it's <code>true</code>, the release was directly retrieved
	 * by its ID.
	 * 
	 * @return The status of this release
	 */
	public boolean isComplete() {
		return this.isComplete;
	}

	/**
	 * Set the complete status of this release
	 * 
	 * @param isComplete
	 *            The complete status. <code>true</code> to flag that this
	 *            release is with full information or <code>false</code>
	 *            otherwise;
	 */
	public void isComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	@Override
	public String toString() {
		return this.title;
	}

	public int compareTo(ArtistRelease another) {
		return this.getYear() - another.getYear();
	}
}
