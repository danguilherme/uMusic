package com.ventura.umusic;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.TextView;

import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.lyrics.LyricNotFoundException;
import com.ventura.umusic.lyrics.provider.LyricProvider;

public class LyricDownloadTask extends AsyncTask<Lyrics, Integer, Lyrics> {
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
	protected Lyrics doInBackground(Lyrics... lyric) {
		try {
			lyric[0] = this.mLyricProvider.searchLyrics(
					lyric[0].getArtistName(), lyric[0].getMusicName());
		} catch (LyricNotFoundException e) {
			lyric[0].setLyrics("Lyric not found...");
			e.printStackTrace();
		}
		return lyric[0];
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
	}

	@Override
	protected void onPostExecute(Lyrics result) {
		this.mTargetLyricComponent.setText(result.getLyrics());
		if (this.mProgressDialog != null && this.mProgressDialog.isShowing()) {
			this.mProgressDialog.dismiss();
		}
	}

}
