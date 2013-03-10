package com.ventura.lyricsfinder.entity;

import java.net.URL;

import com.ventura.lyricsfinder.discogs.entity.enumerator.ExternalUrlTypes;

public class ExternalUrl implements Comparable<ExternalUrl> {
	private ExternalUrlTypes type;
	private URL url;

	public ExternalUrl() {
	}

	public ExternalUrl(ExternalUrlTypes type, URL url) {
		this.type = type;
		this.url = url;
	}

	public ExternalUrlTypes getType() {
		return type;
	}

	public void setType(ExternalUrlTypes type) {
		this.type = type;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL externalUrl) {
		this.url = externalUrl;
	}

	@Override
	public String toString() {
		if (this.url != null) {
			return this.url.toString() + " (" + this.type.toString() + ")";
		}
		return "";
	}

	public int compareTo(ExternalUrl another) {
		return this.getType().compareTo(another.getType());
	}
}
