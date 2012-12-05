package com.ventura.lyricsfinder.musixmatch.ui;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.config.StatusCode;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.TrackData;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;

public class LyricsViewerActivity extends Activity {
	final String TAG = getClass().getName();

	TextView lyricsTextView;
	TextView artistNameTextView;
	TextView musicNameTextView;

	MusixMatch mMusixMatch;
	TrackData mTrack = new TrackData();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lyric_view);

		Intent intent = this.getIntent();
		String artist = intent
				.getStringExtra(GlobalConstants.EXTRA_ARTIST_NAME);
		String music = intent.getStringExtra(GlobalConstants.EXTRA_TRACK_NAME);

		lyricsTextView = (TextView) this.findViewById(R.id.lyrics_text_view);
		artistNameTextView = (TextView) this
				.findViewById(R.id.artist_text_view);
		musicNameTextView = (TextView) this.findViewById(R.id.music_text_view);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mMusixMatch = new MusixMatch(Constants.API_KEY);

		mTrack.setArtistName(artist);
		mTrack.setTrackName(music);
		artistNameTextView.setText(mTrack.getArtistName());
		musicNameTextView.setText(mTrack.getTrackName());
		new GetLyricTask().execute();
	}

	private void fillLyricInfo(Lyrics lyrics) {
		if (mTrack == null) { // Resource not found
			Toast.makeText(this, R.string.message_lyric_not_found,
					Toast.LENGTH_SHORT).show();
			finish();
		} else {
			artistNameTextView.setText(mTrack.getArtistName());
			musicNameTextView.setText(mTrack.getTrackName());
			lyricsTextView.setText(lyrics.getLyricsBody());
			this.findViewById(R.id.progress_bar_container).setVisibility(
					View.INVISIBLE);
		}
	}

	private class GetLyricTask extends AsyncTask<Void, Void, Lyrics> {
		@Override
		protected Lyrics doInBackground(Void... params) {
			Lyrics lyrics = null;
			try {
				mTrack = mMusixMatch.getMatchingTrack(mTrack.getTrackName(),
						mTrack.getArtistName()).getTrack();
				Log.i(TAG, "Track loaded");
				Log.i(TAG,
						"ID = " + mTrack.getTrackId() + " ("
								+ mTrack.getArtistName() + " - "
								+ mTrack.getTrackName() + ")");
				lyrics = mMusixMatch.getLyrics(mTrack.getTrackId());
				Log.i(TAG, "Lyrics loaded");
				Log.i(TAG,
						"ID = " + lyrics.getLyricsId() + "\r\n"
								+ lyrics.getLyricsBody());
			} catch (MusixMatchException e) {
				Log.e(TAG, e.getMessage());
				if (e.getMessage().equals(
						StatusCode.RESOURCE_NOT_FOUND.getStatusMessage())) {
					mTrack = null;
				}
			}
			return lyrics;
		}

		@Override
		protected void onPostExecute(Lyrics result) {
			super.onPostExecute(result);
			fillLyricInfo(result);
		}
	}
}
