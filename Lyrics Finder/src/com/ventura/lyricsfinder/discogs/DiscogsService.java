package com.ventura.lyricsfinder.discogs;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ventura.lyricsfinder.BaseService;
import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.discogs.entity.Artist;
import com.ventura.lyricsfinder.discogs.entity.ArtistRelease;
import com.ventura.lyricsfinder.discogs.entity.SearchResult;
import com.ventura.lyricsfinder.discogs.entity.Track;
import com.ventura.lyricsfinder.discogs.entity.enumerator.QueryType;
import com.ventura.lyricsfinder.discogs.oauth.Constants;
import com.ventura.lyricsfinder.exception.LazyInternetConnectionException;
import com.ventura.lyricsfinder.exception.NoInternetConnectionException;

public class DiscogsService extends BaseService {
	final String TAG = getClass().getName();
	private OAuthConsumer mConsumer;

	public DiscogsService(Context context, OAuthConsumer consumer) {
		super(context);
		this.mConsumer = consumer;
	}

	public SearchResult search(QueryType type, String query)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		if (query != null)
			query = URLEncoder.encode(query);
		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_search);
		url = String.format(Constants.API_REQUEST + url.replace("%26", "&"),
				type, query).toLowerCase();

		JSONObject jsonResponse = this.doGet(url);

		SearchResult searchResult = null;

		if (jsonResponse.optBoolean(this.KEY_SUCCESS)) {
			String searchResults = jsonResponse.optString(this.KEY_DATA);
			if (searchResults == null || searchResults == "")
				return new SearchResult();

			try {
				searchResult = new SearchResult(type, new JSONObject(
						searchResults));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return searchResult;
	}

	public Artist getArtistInfo(int artistId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_artists);
		url = String.format(Constants.API_REQUEST + url,
				String.valueOf(artistId));

		JSONObject jsonResponse = this.doGet(url);

		Artist artist = null;
		if (jsonResponse.optBoolean(this.KEY_SUCCESS)) {
			String artistInfo = jsonResponse.optString(this.KEY_DATA);

			try {
				artist = new Artist(new JSONObject(artistInfo));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return artist;
	}

	public List<ArtistRelease> getArtistReleases(int artistId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_releases);
		url = String.format(Constants.API_REQUEST + url,
				String.valueOf(artistId));

		JSONObject jsonResponse = this.doGet(url);

		if (jsonResponse.optBoolean(this.KEY_SUCCESS)) {
			try {
				// Needs this because the response in 'data' comes as string
				jsonResponse = new JSONObject(
						jsonResponse.optString(this.KEY_DATA));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		List<ArtistRelease> releases = new ArrayList<ArtistRelease>();

		try {
			JSONArray releasesJsonArray = jsonResponse
					.getJSONArray(ArtistRelease.KEY_SEARCH_RESULT_RELEASES);

			for (int i = 0; i < releasesJsonArray.length(); i++) {
				releases.add(new ArtistRelease(releasesJsonArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return releases;
	}

	public List<Track> getReleaseTracks(ArtistRelease release)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = null;

		switch (release.getType()) {
		case Master:
			url = res.getString(R.string.discogs_url_tracks_masters);
			break;
		case Release:
			url = res.getString(R.string.discogs_url_tracks_releases);
			break;
		}

		url = String.format(Constants.API_REQUEST + url,
				String.valueOf(release.getId()));

		JSONObject jsonResponse = this.doGet(url);
		
		List<Track> trackList = new ArrayList<Track>();
		
		try {
			if (jsonResponse.getBoolean(KEY_SUCCESS)) {
				jsonResponse =  new JSONObject(jsonResponse.getString(KEY_DATA));
				
				JSONArray trackListJsonArray = jsonResponse.getJSONArray("tracklist");
				
				for (int i = 0; i < trackListJsonArray.length(); i++) {
					trackList.add(new Track(trackListJsonArray.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return trackList;
	}

	@Override
	protected JSONObject doGet(String url)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		HttpGet request = new HttpGet(url);
		Log.i(TAG, "Requesting URL : " + url);

		try {
			this.mConsumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

		return this.doGet(request);
	}
}
