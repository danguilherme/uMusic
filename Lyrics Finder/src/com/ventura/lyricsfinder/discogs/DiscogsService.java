package com.ventura.lyricsfinder.discogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.oauth.Constants;

public class DiscogsService {
	final String TAG = getClass().getName();
	private Context mContext;

	public DiscogsService(Context context) {
		this.mContext = context;
	}

	public JSONArray search(String type, String query, OAuthConsumer consumer)
			throws JSONException {
		if (query != null)
			query = URLEncoder.encode(query);
		Resources res = this.mContext.getResources();
		String url = res.getString(R.string.discogs_url_search);
		url = String.format(Constants.API_REQUEST + url.replace("%26", "&"), type, query);

		String searchResults = this.doGet(url, consumer);
		JSONObject searchResultsObject = new JSONObject(searchResults);
		JSONArray searchResultsArray = searchResultsObject
				.getJSONArray(DiscogsConstants.KEY_RESULTS);
		return searchResultsArray;
	}
	
	public JSONObject getArtistInfo(String artistId, OAuthConsumer consumer)
			throws JSONException {
		Resources res = this.mContext.getResources();
		String url = res.getString(R.string.discogs_url_artists);
		url = String.format(Constants.API_REQUEST + url, artistId);

		String artistInfo = this.doGet(url, consumer);
		JSONObject artistInfoObject = new JSONObject(artistInfo);
		return artistInfoObject;
	}

	private String doGet(String url, OAuthConsumer consumer) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		Log.i(TAG, "Requesting URL : " + url);
		try {
			consumer.sign(request);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}

		HttpResponse response = null;
		try {
			response = httpclient.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response.getStatusLine().getStatusCode() != 200) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("success", false);
				obj.put("message", response.getStatusLine().getReasonPhrase());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return obj.toString();
		}
		Log.i(TAG, "Statusline : " + response.getStatusLine());
		InputStream data;
		StringBuilder responseBuilder = null;
		try {
			data = response.getEntity().getContent();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(data));
			String responeLine;
			responseBuilder = new StringBuilder();
			while ((responeLine = bufferedReader.readLine()) != null) {
				responseBuilder.append(responeLine);
			}
			Log.i(TAG, "Response : " + responseBuilder.toString());
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseBuilder.toString();
	}
}
