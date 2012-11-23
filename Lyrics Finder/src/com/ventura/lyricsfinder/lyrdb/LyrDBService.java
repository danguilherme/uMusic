package com.ventura.lyricsfinder.lyrdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.webkit.DownloadListener;

import com.ventura.lyricsfinder.LyricDownloadTask;
import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.lyrdb.entities.Lyric;

public class LyrDBService {

	final String TAG = getClass().getName();
	private Context mContext;

	public LyrDBService(Context context) {
		this.mContext = context;
	}

	public String getLyric(String id) {
		Resources res = this.mContext.getResources();
		String url = String.format(
				res.getString(R.string.lyrdb_url_base)
						+ res.getString(R.string.lyrdb_url_lyrics), id);

		String lyric = null;
		try {
			lyric = this.doGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lyric;
	}

	public List<Lyric> search(QueryType type, String artistName,
			String musicName) {
		artistName = URLEncoder.encode(artistName);
		musicName = URLEncoder.encode(musicName);
		Resources res = this.mContext.getResources();
		String url = String.format(res.getString(R.string.lyrdb_url_base)
				+ res.getString(R.string.lyrdb_url_search).replace("%26", "&"),
				artistName, musicName, type.toString().toLowerCase());

		String searchResults = null;
		try {
			searchResults = this.doGet(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Lyric> foundLyrics = new ArrayList<Lyric>();
		if (searchResults.equals("")) {
			return foundLyrics;
		}
		String[] foundLyricsLines = searchResults.split("\r\n");
		for (int i = 0; i < foundLyricsLines.length; i++) {
			foundLyricsLines[i] = foundLyricsLines[i].replace("\\", "~");
			String[] prop = foundLyricsLines[i].split("~");
			foundLyrics.add(new Lyric(prop[0].toString(), prop[1].toString(),
					prop[2].toString()));
		}
		return foundLyrics;
	}

	private String doGet(String url) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.addHeader("Accept", "text/txt");
		Log.i(TAG, "Requesting URL : " + url);

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
			String responseLine;
			responseBuilder = new StringBuilder();
			boolean firstLine = true;
			boolean wasError = false;

			while ((responseLine = bufferedReader.readLine()) != null) {
				if (wasError) {
					throw new Exception(responseLine);
				}
				if (firstLine && responseLine.toLowerCase().equals("error")) {
					wasError = true;
				}
				firstLine = false;

				responseBuilder.append(responseLine);
				responseBuilder.append("\r\n");
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
