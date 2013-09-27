package com.ventura.umusic.ui.music;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.umusic.R;
import com.ventura.umusic.business.LyricsService;
import com.ventura.umusic.constants.GlobalConstants;
import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.entity.music.Track;
import com.ventura.umusic.music.TracksManager;
import com.ventura.umusic.ui.BaseActivity;

@EActivity(R.layout.activity_lyrics_view)
public class LyricsViewerActivity extends BaseActivity {
	final String TAG = getClass().getName();

	@ViewById(R.id.lyrics_text)
	TextView lyricsText;

	@ViewById(R.id.artist_name)
	EditText artistName;

	@ViewById(R.id.song_name)
	EditText songName;

	@ViewById(R.id.search_lyrics)
	Button searchLyrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActionBar actionBar = this.getSupportActionBar();
		// actionBar.
	}

	/**
	 * Responsible to get artist and song names from wherever they comes in the
	 * Intent.
	 */
	private void fetchLyricsInfo() {
		Intent intent = this.getIntent();
		String action = intent.getAction();
		Bundle extras = intent.getExtras();

		if (Intent.ACTION_SEND.equals(action)) {
			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

			TracksManager tracksManager = new TracksManager(this);
			Track track = tracksManager.getTrackByUri(uri.toString());

			if (track != null) {
				setArtistName(track.getArtist().getName());
				setSongName(track.getTitle());
			}
		} else {
			setArtistName(extras.getString(GlobalConstants.EXTRA_ARTIST_NAME));
			setSongName(extras.getString(GlobalConstants.EXTRA_TRACK_NAME));
		}
	}

	public String getArtistName() {
		return artistName.getText().toString();
	}

	public void setArtistName(String artistName) {
		this.artistName.setText(artistName);
	}

	public String getSongName() {
		return songName.getText().toString();
	}

	public void setSongName(String songName) {
		this.songName.setText(songName);
	}

	@AfterViews
	public void onAfterViews() {
		fetchLyricsInfo();
		if (getSongName() == "" || getArtistName() == "") {
			finish();
			return;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getSongName());
		actionBar.setSubtitle(getArtistName());

		getLyrics();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Set params")
			.setIcon(R.drawable.ic_action_search)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Background
	@Click(R.id.search_lyrics)
	public void getLyrics() {
		LyricsService lyricsService = new LyricsService(this);

		try {
			showToast("Carregando...", Toast.LENGTH_SHORT);
			Lyrics lyrics = lyricsService.getLyrics(getArtistName(),
					getSongName());
			updateView(lyrics);
			showToast("Carregado!", Toast.LENGTH_SHORT);
		} catch (NoInternetConnectionException e) {
			alert(e.getMessage());
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			alert(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			alert(e.getMessage());
			e.printStackTrace();
		}
	}

	@UiThread
	public void updateView(Lyrics lyric) {
		lyricsText.setText(lyric.getLyricsText());
	}
}
