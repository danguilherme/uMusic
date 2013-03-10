package com.ventura.lyricsfinder.business;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.musicexplorer.R;
import com.ventura.lyricsfinder.entity.artist.Artist;
import com.ventura.lyricsfinder.entity.release.ArtistRelease;

public class ArtistService extends BaseService {
	public ArtistService(Context context) {
		super(context);
	}

	public List<Artist> search(String query)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String url = String.format(
				this.getStringResource(R.string.search_artist_url), query);

		
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

		String url = String
				.format(this.getStringResource(R.string.get_artist_by_id_url),
						artistId);

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

		String requestUrl = String.format(
				this.getStringResource(R.string.get_release_by_artist_id_url),
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
