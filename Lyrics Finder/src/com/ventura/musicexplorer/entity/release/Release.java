package com.ventura.musicexplorer.entity.release;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.ventura.musicexplorer.entity.artist.Artist;

public class Release extends ArtistRelease {
	private static final long serialVersionUID = -8772328584459335059L;

	// ???
	private String releasedFormatted;
	private int masterId;
	private URL masterUrl;

	private List<Label> labels = new ArrayList<Label>();
	private List<Artist> extraArtists = new ArrayList<Artist>();
	private String country;

	public String getReleasedFormatted() {
		return releasedFormatted;
	}

	public void setReleasedFormatted(String releasedFormatted) {
		this.releasedFormatted = releasedFormatted;
	}

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

	public URL getMasterUrl() {
		return masterUrl;
	}

	public void setMasterUrl(URL masterUrl) {
		this.masterUrl = masterUrl;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public List<Artist> getExtraArtists() {
		return extraArtists;
	}

	public void setExtraArtists(List<Artist> extraArtists) {
		this.extraArtists = extraArtists;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
