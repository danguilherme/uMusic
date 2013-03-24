package com.ventura.musicexplorer.business;

import android.content.Context;

import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.musicexplorer.entity.release.Master;
import com.ventura.musicexplorer.entity.release.Release;

public class ReleaseService extends BaseService {

	protected static final String URL_GET_RELEASE_BY_ID = "/releases/%1$s";
	protected static final String URL_GET_MASTER_BY_ID = "/releases/getmaster/%1$s";

	public ReleaseService(Context context) {
		super(context);
	}

	public Release getRelease(int releaseId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String requestUrl = String.format(ReleaseService.URL_BASE_API
				+ ReleaseService.URL_GET_RELEASE_BY_ID,
				String.valueOf(releaseId));

		String jsonResponse = this.doGet(requestUrl);

		Release release = null;

		try {
			release = this.deserialize(jsonResponse, Release.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return release;
	}

	public Master getMaster(int masterId) throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String requestUrl = String.format(ReleaseService.URL_BASE_API
				+ ReleaseService.URL_GET_MASTER_BY_ID,
				String.valueOf(masterId));

		String jsonResponse = this.doGet(requestUrl);

		Master master = null;

		try {
			master = this.deserialize(jsonResponse, Master.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return master;
	}
}
