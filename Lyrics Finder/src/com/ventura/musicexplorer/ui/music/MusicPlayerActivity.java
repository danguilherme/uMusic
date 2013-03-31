package com.ventura.musicexplorer.ui.music;

import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.music.Music;
import com.ventura.musicexplorer.music.Song;
import com.ventura.musicexplorer.music.SongsManager;
import com.ventura.musicexplorer.ui.BaseActivity;

@EActivity(R.layout.musicplayer)
public class MusicPlayerActivity extends BaseActivity implements
		OnCompletionListener {
	final String TAG = getClass().getName();
	SongsManager songsManager;
	List<Song> songsList;
	Music music;

	@ViewById(R.id.btnPlay)
	ImageButton btnPlay;
	@ViewById(R.id.btnPause)
	ImageButton btnPause;
	@ViewById(R.id.btnNext)
	ImageButton btnNext;
	@ViewById(R.id.btnPrevious)
	ImageButton btnPrevious;

	@ViewById(R.id.songTitle)
	TextView lblSongTitle;
	@ViewById(R.id.artist_name)
	TextView lblArtistName;

	@ViewById(R.id.songProgressBar)
	SeekBar songProgressBar;
	@ViewById(R.id.songCurrentDurationLabel)
	TextView lblSongCurrentDuration;
	@ViewById(R.id.songTotalDurationLabel)
	TextView lblSongTotalDurationLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		music = new Music(this);
		music.setOnCompletionListener(this);
		songsManager = new SongsManager(this);
		songsList = songsManager.refreshPlayList();
		// alert(this.createList(songs));
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		next();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
	}

	@AfterViews
	void afterViews() {
		this.playSong(songsList.get(0));
	}

	private void playSong(Song song) {
		btnPlay.setVisibility(View.GONE);
		btnPause.setVisibility(View.VISIBLE);

		lblSongTitle.setText(song.getTitle());
		lblArtistName.setText("No artist... :(");

		if (song != null) {
			music.play(song);
		}
	}

	@Click(R.id.btnPlay)
	void play() {
		if (music.getCurrentPlaying() != null) {
			this.playSong(music.getCurrentPlaying());
		}
	}

	@Click(R.id.btnPause)
	void pause() {
		btnPlay.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.GONE);
		music.pause();
	}

	@Click(R.id.btnNext)
	void next() {
		int indexOfCurrent = songsList.indexOf(music.getCurrentPlaying());
		if (indexOfCurrent < songsList.size() - 1) {
			this.playSong(songsList.get(indexOfCurrent + 1));
		} else {
			this.playSong(songsList.get(0));
		}
	}

	@Click(R.id.btnPrevious)
	void prev() {
		int indexOfCurrent = songsList.indexOf(music.getCurrentPlaying());
		if (indexOfCurrent > 0) {
			this.playSong(songsList.get(indexOfCurrent - 1));
		} else {
			this.playSong(songsList.get(songsList.size() - 1));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		music.dispose();
	}
}
