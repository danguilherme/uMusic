package com.ventura.lyricsfinder;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import com.ventura.lyricsfinder.lyrics.Lyric;
import com.ventura.lyricsfinder.lyrics.LyricNotFoundException;
import com.ventura.lyricsfinder.lyrics.provider.LyricProvider;

public class LyricDownloadTask extends AsyncTask<Lyric, Integer, Lyric> {
	private LyricProvider mLyricProvider;
	private TextView mTargetLyricComponent;
	private ProgressDialog mProgressDialog;

	public LyricProvider getLyricProvider() {
		return mLyricProvider;
	}

	public TextView getTargetLyricComponent() {
		return mTargetLyricComponent;
	}

	public ProgressDialog getProgressDialog() {
		return mProgressDialog;
	}

	public LyricDownloadTask(LyricProvider lyricProvider,
			TextView targetComponent, ProgressDialog progressDialog) {
		this.mLyricProvider = lyricProvider;
		this.mTargetLyricComponent = targetComponent;
		this.mProgressDialog = progressDialog;
	}

	@Override
	protected Lyric doInBackground(Lyric... lyric) {
		try {
			lyric[0] = this.mLyricProvider.searchLyrics(
					lyric[0].getArtistName(), lyric[0].getMusicName());
		} catch (LyricNotFoundException e) {
			lyric[0].setLyric("Lyric not found...");
			e.printStackTrace();
		}
		return lyric[0];
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
	}

	@Override
	protected void onPostExecute(Lyric result) {
		this.mTargetLyricComponent.setText(result.getLyric());
		if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
			this.mProgressDialog.dismiss();
		}
	}

}
