package com.ventura.musicexplorer.lyrics.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ventura.musicexplorer.entity.music.Lyrics;
import com.ventura.musicexplorer.lyrics.LyricNotFoundException;

public abstract class LyricProvider {
	private int mId;
	private String mUrl;
	private String mName;
	private LyricProviders mCode;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public void setCode(LyricProviders mCode) {
		this.mCode = mCode;
	}

	public LyricProviders getCode() {
		return mCode;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getName() {
		return mName;
	}

	public Lyrics searchLyrics(String artistName, String musicName)
			throws LyricNotFoundException {
		Lyrics lyric = new Lyrics(artistName, musicName);
		String stringResponse = null;
		try {
			this.setUrl(this.encodeToUrl(this.getUrl()));
			String url = String.format(this.getUrl(),
					this.encodeToUrl(artistName), this.encodeToUrl(musicName));
			url = validateUrl(url);
			stringResponse = this.requestLyricsHtml(url);
		} catch (ClientProtocolException e1) {
			System.out.println(e1.getMessage());
		} catch (IOException e2) {
			System.out.println(e2.getMessage());
		}

		lyric.setArtistName(artistName.trim());
		lyric.setMusicName(musicName.trim());
		if (stringResponse == null || stringResponse.equals("")) {
			lyric.setLyrics("Lyric not found");
		} else {
			String lyricHtml = this.decodeLyricHtml(stringResponse, lyric);
			URLDecoder.decode(lyricHtml);
			lyric.setLyrics(this.insertLineBreak(lyricHtml));
		}
		return lyric;
	}

	private StringBuilder inputStreamToString(InputStream is)
			throws IOException {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		while ((line = rd.readLine()) != null) {
			total.append(line);
		}

		// Return full string
		return total;
	}

	public String requestLyricsHtml(String url) throws IllegalStateException,
			IOException {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpGet);
		StringBuilder stringResponse = this.inputStreamToString(response
				.getEntity().getContent());

		return stringResponse.toString();
	}

	private String insertLineBreak(String html) {
		return html.replace("<br />", "\r\n").replace("<br/>", "\r\n")
				.replace("<br>", "\r\n");
	}

	private String validateUrl(String url) {
		url = url.replaceAll("(ã|â|à|á|ì|í|õ|ó|')", "");
		return url;
	}

	public abstract String decodeLyricHtml(String html, Lyrics actualLyric)
			throws LyricNotFoundException;

	public abstract String encodeToUrl(String target);
}
