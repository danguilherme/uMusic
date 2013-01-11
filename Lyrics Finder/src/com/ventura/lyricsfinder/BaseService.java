package com.ventura.lyricsfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.ventura.lyricsfinder.exception.LazyInternetConnectionException;
import com.ventura.lyricsfinder.exception.NoInternetConnectionException;
import com.ventura.lyricsfinder.util.ConnectionManager;

public class BaseService {
	final String TAG = getClass().getName();

	public final String KEY_DATA = "data";
	public final String KEY_SUCCESS = "success";
	public final String KEY_MESSAGE = "message";

	private Context mContext;

	public BaseService(Context context) {
		this.mContext = context;
	}

	protected String doGet(String url)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		
		if (!new ConnectionManager(this.mContext).isConnected())
			throw new NoInternetConnectionException();
		HttpGet request = null;
		try {
			request = new HttpGet(url);			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.doGet(request);
	}

	protected String doGet(HttpGet request)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {
		
		if (!new ConnectionManager(this.mContext).isConnected())
			throw new NoInternetConnectionException();

		DefaultHttpClient httpClient = new DefaultHttpClient();
		
		Log.i(TAG, "Requesting URL : " + request.getURI());

		HttpResponse response = null;
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			throw new LazyInternetConnectionException();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (response == null)
			return null;
		
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

	public Context getContext() {
		return this.mContext;
	}
}
