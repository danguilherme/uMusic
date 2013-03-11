package com.ventura.lyricsfinder.discogs;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import com.google.gson.Gson;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.lyricsfinder.business.BaseService;
import com.ventura.lyricsfinder.discogs.entity.Artist;
import com.ventura.lyricsfinder.discogs.entity.ArtistRelease;
import com.ventura.lyricsfinder.discogs.entity.Master;
import com.ventura.lyricsfinder.discogs.entity.Paging;
import com.ventura.lyricsfinder.discogs.entity.Release;
import com.ventura.lyricsfinder.discogs.entity.SearchResult;
import com.ventura.lyricsfinder.discogs.entity.enumerator.QueryType;
import com.ventura.lyricsfinder.discogs.entity.enumerator.SpecialEnums;
import com.ventura.lyricsfinder.discogs.oauth.Constants;
import com.ventura.musicexplorer.R;

public class DiscogsService extends BaseService {
	final String TAG = getClass().getName();

	private OAuthConsumer mConsumer;
	Gson deserializer;

	public DiscogsService(Context context, OAuthConsumer consumer) {
		super(context);
		this.mConsumer = consumer;
		this.deserializer = new Gson();
	}

	public SearchResult search(QueryType type, String query)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		if (query != null)
			query = URLEncoder.encode(query);
		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_search);
		url = String.format(
				Constants.DISCOGS_API_REQUEST + url.replace("%26", "&"), type,
				query).toLowerCase(Locale.US);

		String json = this.doGet(url);

		SearchResult searchResult = null;
		if (json == null || json.equals(""))
			return new SearchResult();
		try {
			searchResult = deserializer.fromJson(json, SearchResult.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return searchResult;
	}

	public Artist getArtistInfo(int artistId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_artists);
		url = String.format(Constants.DISCOGS_API_REQUEST + url,
				String.valueOf(artistId));

		String jsonResponse = this.doGet(url);

		Artist artist = null;

		try {
			// Workaround to handle the error of urls coming empty.
			// See http://api.discogs.com/artists/8760, inside the
			// 'urls' property
			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONArray urlsArray = jsonObject
					.optJSONArray(Artist.KEY_EXTERNAL_URLS);
			if (urlsArray != null) {
				JSONArray correctUrlsArray = new JSONArray();
				for (int i = 0; i < urlsArray.length(); i++) {
					if (!urlsArray.getString(i).equals("")) {
						correctUrlsArray.put(urlsArray.getString(i));
					}
				}
				jsonObject.put(Artist.KEY_EXTERNAL_URLS, correctUrlsArray);
				jsonResponse = jsonObject.toString();
			}

			artist = deserializer.fromJson(jsonResponse, Artist.class);
			artist.fillExternalUrlsList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return artist;
	}

	public List<ArtistRelease> getArtistReleases(int artistId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_releases);
		url = String.format(Constants.DISCOGS_API_REQUEST + url,
				String.valueOf(artistId));

		String jsonResponse = this.doGet(url);

		List<ArtistRelease> releases = new ArrayList<ArtistRelease>();

		try {
			JSONArray releasesJsonArray = new JSONObject(jsonResponse)
					.getJSONArray(ArtistRelease.KEY_SEARCH_RESULT_RELEASES);

			ArtistRelease singleRelease;
			for (int i = 0; i < releasesJsonArray.length(); i++) {
				singleRelease = deserializer.fromJson(releasesJsonArray
						.getJSONObject(i).toString(), Release.class);
				singleRelease.loadThumb();
				releases.add(singleRelease);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return releases;
	}

	public Release getRelease(ArtistRelease artistRelease)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_tracks_releases);

		url = String.format(Constants.DISCOGS_API_REQUEST + url,
				String.valueOf(artistRelease.getId()));

		String jsonResponse = this.doGet(url);

		Release release = null;

		try {
			release = deserializer.fromJson(jsonResponse, Release.class);
			release.loadThumb();
			release.setType(SpecialEnums.ARTIST_RELEASE_TYPE_RELEASE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return release;
	}

	public Master getMaster(ArtistRelease artistRelease)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		Resources res = this.getContext().getResources();
		String url = res.getString(R.string.discogs_url_tracks_masters);

		url = String.format(Constants.DISCOGS_API_REQUEST + url,
				String.valueOf(artistRelease.getId()));

		String jsonResponse = this.doGet(url);

		Master master = null;

		try {
			master = deserializer.fromJson(jsonResponse, Master.class);
			master.loadThumb();
			master.setType(SpecialEnums.ARTIST_RELEASE_TYPE_MASTER);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return master;
	}

	@Override
	protected String doGet(String url) throws NoInternetConnectionException,
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

	public SearchResult navigate(String url)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		String targetUrl = url;

		if (targetUrl == null || targetUrl.equals("")) {
			return null;
		}

		String json = this.doGet(targetUrl);

		SearchResult searchResult = null;
		if (json == null || json.equals(""))
			return new SearchResult();
		try {
			searchResult = deserializer.fromJson(json, SearchResult.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return searchResult;
	}

	public SearchResult next(Paging paging)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		return navigate(paging.getPageNavigation().getNextPageUrl().toString());
	}

	protected SearchResult prev(Paging paging)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		return navigate(paging.getPageNavigation().getPreviousPageUrl()
				.toString());
	}

	protected SearchResult last(Paging paging)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		return navigate(paging.getPageNavigation().getLastPageUrl().toString());
	}

	protected SearchResult first(Paging paging)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		return navigate(paging.getPageNavigation().getFirstPageUrl().toString());
	}
}
