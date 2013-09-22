package com.ventura.umusic.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.umusic.entity.artist.Artist;
import com.ventura.umusic.entity.release.ArtistRelease;

public class ArtistService extends BaseService {
	public ArtistService(Context context) {
		super(context);
	}

	protected static final String URL_LIST_ARTISTS = "/artists/search?q=%1$s";
	protected static final String URL_GET_ARTIST_BY_ID = "/artists/%1$s";
	protected static final String URL_GET_RELEASES_BY_ARTIST_ID = "/artists/listreleases/%1$s";

	public List<Artist> search(String query)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String url = String.format(ArtistService.URL_BASE_API
				+ ArtistService.URL_LIST_ARTISTS, query);

		String jsonResponse = this.doGet(url);

		List<Artist> artists = new ArrayList<Artist>();

		try {
			artists = this.deserializeList(this.extractData(jsonResponse),
					Artist.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return artists;
	}

	public Artist getArtist(int artistId) throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String url = String.format(ArtistService.URL_BASE_API
				+ ArtistService.URL_GET_ARTIST_BY_ID, artistId);

		String jsonResponse = this.doGet(url);

		Artist artist = null;
		try {
			artist = this.deserialize(jsonResponse, Artist.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return artist;
	}

	public List<ArtistRelease> getArtistReleases(int artistId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String requestUrl = String.format(ArtistService.URL_BASE_API
				+ ArtistService.URL_GET_RELEASES_BY_ARTIST_ID,
				String.valueOf(artistId));

		String jsonResponse = this.doGet(requestUrl);

		List<ArtistRelease> releases = new ArrayList<ArtistRelease>();

		try {
			releases = this.deserializeList(this.extractData(jsonResponse),
					ArtistRelease.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return releases;
	}
}
