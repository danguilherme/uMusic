package com.ventura.musicexplorer.entity;

import java.io.Serializable;
import java.net.URL;

import com.ventura.musicexplorer.discogs.entity.enumerator.ExternalUrlTypes;

public class ExternalUrl implements Comparable<ExternalUrl>, Serializable {
	private static final long serialVersionUID = 4059668315765780683L;
	
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
