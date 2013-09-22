package com.ventura.umusic.discogs.entity;

import java.net.URL;
import java.util.Locale;

import com.ventura.umusic.discogs.entity.enumerator.ExternalUrlTypes;

public class ExternalUrl implements Comparable<ExternalUrl> {
	private ExternalUrlTypes mType;
	private URL mExternalUrl;
	private Artist mArtist;

	public ExternalUrl() {
	}

	public ExternalUrl(ExternalUrlTypes type, URL url, Artist artist) {
		this.mType = type;
		this.mExternalUrl = url;
		this.mArtist = artist;
	}

	/**
	 * Constructor that decodes the url and discover its
	 * <code>ExternalUrlTypes</code>.
	 * 
	 * @param url
	 *            The URL to decode
	 */
	public ExternalUrl(URL url, Artist artist) {
		ExternalUrl externalUrl = ExternalUrl.decode(url, artist);
		this.mArtist = artist;
		this.mExternalUrl = url;
		this.mType = externalUrl.getType();
	}

	public static ExternalUrl decode(URL url, Artist artist) {
		String sURL = url.toString();
		ExternalUrl externalUrl = new ExternalUrl();

		if (artist != null
				&& (sURL.contains(artist.getName().replace(" ", "")
						.toLowerCase(Locale.ENGLISH)
						+ ".com")
						|| sURL.contains(artist.getName().replace(" ", "-")
								.toLowerCase(Locale.ENGLISH)
								+ ".com") || sURL.contains(artist.getName()
						.replace(" ", "").toLowerCase(Locale.ENGLISH)
						+ ".net"))) {
			// is a official website
			externalUrl.setType(ExternalUrlTypes.ArtistWebsite);
		} else if (sURL.contains("youtube.com")) {
			// is a Youtube url
			externalUrl.setType(ExternalUrlTypes.YouTube);
		} else if (sURL.contains("wikipedia.org")) {
			// is a Wikipedia url
			externalUrl.setType(ExternalUrlTypes.Wikipedia);
		} else if (sURL.contains("facebook.com")) {
			// is a Facebook url
			externalUrl.setType(ExternalUrlTypes.Facebook);
		} else if (sURL.contains("twitter.com")) {
			// is a Twitter url;
			externalUrl.setType(ExternalUrlTypes.Twitter);
		} else if (sURL.contains("myspace.com")) {
			// is a My Space url;
			externalUrl.setType(ExternalUrlTypes.MySpace);
		} else if (sURL.contains("tumblr.com")) {
			// is a Tumblr url;
			externalUrl.setType(ExternalUrlTypes.Tumblr);
		} else if (sURL.contains("plus.google.com")) {
			// is a Google+ url;
			externalUrl.setType(ExternalUrlTypes.GooglePlus);
		} else if (sURL.contains("vimeo.com")) {
			// is a Vimeo url;
			externalUrl.setType(ExternalUrlTypes.Vimeo);
		} else {
			// is only a url...
			externalUrl.setType(ExternalUrlTypes.OtherWebsite);
		}
		return externalUrl;
	}

	public ExternalUrlTypes getType() {
		return mType;
	}

	public void setType(ExternalUrlTypes mType) {
		this.mType = mType;
	}

	public URL getExternalUrl() {
		return mExternalUrl;
	}

	public void setExternalUrl(URL mExternalUrl) {
		this.mExternalUrl = mExternalUrl;
	}

	public Artist getArtist() {
		return mArtist;
	}

	public void setArtist(Artist artist) {
		this.mArtist = artist;
		artist.getExternalUrls().add(this);
	}

	@Override
	public String toString() {
		if (this.mExternalUrl != null) {
			return this.mExternalUrl.toString() + " (" + this.mType.toString()
					+ ")";
		}
		return "";
	}

	public int compareTo(ExternalUrl another) {
		return this.getType().compareTo(another.getType());
	}
}
