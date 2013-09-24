package com.ventura.umusic.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.umusic.R;
import com.ventura.umusic.business.LyricsService;
import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.entity.music.Track;
import com.ventura.umusic.music.TracksManager;

@EActivity(R.layout.activity_lyrics_view)
public class LyricsViewerActivity extends BaseActivity {
	final String TAG = getClass().getName();

	@ViewById(R.id.lyrics_text)
	TextView lyricsText;

	Track mTrack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	public void onAfterViews() {
		TracksManager tracks = new TracksManager(this);
		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

		mTrack = tracks.getTrackByUri(uri.toString());
		
		if (mTrack == null) {
			finish();
			return;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setSubtitle(this.getTitle());
		actionBar.setTitle((mTrack.getArtist() != null  ? mTrack.getArtist().getName() : "") + " - " + mTrack.getTitle());

		getLyrics();
	}

	@Background
	public void getLyrics() {
		LyricsService lyricsService = new LyricsService(this);

		try {
			showToast("Carregando...", Toast.LENGTH_SHORT);
			mTrack.setLyrics(lyricsService.getLyrics(mTrack.getArtist()
					.getName(), mTrack.getTitle()));
			updateView(mTrack.getLyrics());
			showToast("Carregado!", Toast.LENGTH_SHORT);
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
			alert(e.getMessage());
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
			alert(e.getMessage());
		}
	}

	@UiThread
	public void updateView(Lyrics lyric) {
		lyricsText.setText(lyric.getLyricsText());
	}
}
