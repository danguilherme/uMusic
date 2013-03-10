package com.ventura.lyricsfinder.business;

import android.content.Context;

import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.musicexplorer.R;
import com.ventura.lyricsfinder.entity.release.Release;

public class ReleaseService extends BaseService {

	public ReleaseService(Context context) {
		super(context);
	}

	public Release getRelease(int releaseId)
			throws NoInternetConnectionException,
			LazyInternetConnectionException {

		String requestUrl = String.format(
				this.getStringResource(R.string.get_release_by_id_url),
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
}
