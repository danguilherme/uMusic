package com.ventura.musicexplorer.ui.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.entity.music.Track;
import com.ventura.musicexplorer.lyrdb.LyrDBService;
import com.ventura.musicexplorer.lyrdb.QueryType;
import com.ventura.musicexplorer.lyrdb.entities.Lyric;
import com.ventura.musicexplorer.music.Music;
import com.ventura.musicexplorer.music.TracksManager;
import com.ventura.musicexplorer.ui.BaseActivity;
import com.ventura.musicexplorer.util.TimerUtils;

@EActivity(R.layout.musicplayer)
public class MusicPlayerActivity extends BaseActivity implements
		OnCompletionListener, OnSeekBarChangeListener {
	final String TAG = getClass().getName();

	final String PREF_IS_SHUFFLE = this.getClass().getName()
			+ ".PREF_IS_SHUFFLE";
	final String PREF_IS_REPEAT = this.getClass().getName() + ".PREF_IS_REPEAT";

	TracksManager songsManager;
	private List<Track> songsList;
	/**
	 * List used when shuffle is active. This way, when click previous, the same
	 * song is played, avoiding a random song again.
	 */
	private List<Track> shuffledSongsList;

	Music music;
	TimerUtils timerUtils;

	boolean isRepeat = false, isShuffle = false;

	@ViewById(R.id.btnPlaylist)
	ImageButton btnPlaylist;

	@ViewById(R.id.btnPlay)
	ImageButton btnPlay;

	@ViewById(R.id.btnPause)
	ImageButton btnPause;

	@ViewById(R.id.btnNext)
	ImageButton btnNext;

	@ViewById(R.id.btnPrevious)
	ImageButton btnPrevious;

	@ViewById(R.id.btnRepeat)
	ImageButton btnRepeat;

	@ViewById(R.id.btnShuffle)
	ImageButton btnShuffle;

	@ViewById(R.id.songTitle)
	TextView lblSongTitle;

	@ViewById(R.id.artist_name)
	TextView lblArtistName;

	@ViewById(R.id.songProgressBar)
	SeekBar songProgressBar;

	@ViewById(R.id.songCurrentDurationLabel)
	TextView lblSongCurrentDuration;

	@ViewById(R.id.songTotalDurationLabel)
	TextView lblSongTotalDuration;

	@ViewById(R.id.songThumbnail)
	ImageView songThumbnail;

	@ViewById(R.id.songLyrics)
	TextView lblSongLyrics;

	@ViewById(R.id.lyricsLoadingProgressBar)
	ProgressBar lyricsLoadingProgressBar;

	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		music = new Music(this);
		timerUtils = new TimerUtils();
		music.setOnCompletionListener(this);
		songsManager = new TracksManager(this);
		songsList = songsManager.refreshPlayList();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		next();
	}

	@AfterViews
	void afterViews() {
		songProgressBar.setOnSeekBarChangeListener(this); // Important

		setRepeat(this.sharedPreferences.getBoolean(PREF_IS_REPEAT, false));
		setShuffle(this.sharedPreferences.getBoolean(PREF_IS_SHUFFLE, false));

		Intent intent = getIntent();
		String intendedAction = intent.getAction();

		if (intendedAction != null && intendedAction.equals(Intent.ACTION_VIEW)) {
			Track track = songsManager.getTrackByUri(intent.getData().getPath());
			songsList.add(track);
			if (shuffledSongsList != null) {
				shuffledSongsList.add(track);
			}
			this.playSong(track);
		} else {
			this.playSong(songsList.get(0));
		}
	}

	// TEST
	@Click(R.id.btnPlaylist)
	void onPlaylistButtonClick(View btn) {
		if (!this.isConnected()) {
			Toast.makeText(this, R.string.message_no_internet_connection,
					Toast.LENGTH_SHORT).show();
			return;
		}

		lyricsLoadingProgressBar.setVisibility(View.VISIBLE);

		searchSongLyrics();
	}

	@Background
	void searchSongLyrics() {
		Track song = music.getCurrentPlaying();
		LyrDBService lyrDBService = new LyrDBService(this);
		List<Lyric> lyrs = lyrDBService.search(QueryType.FullT, new Lyric(null,
				song.getTitle(), song.getArtist().getName()));
		if (lyrs.size() > 0) {
			Lyric lyrics = lyrs.get(0);
			// Verify if the user didn't changed the music
			if (song == music.getCurrentPlaying())
				lyrics.setLyric(lyrDBService.getLyric(lyrics.getId()));
			if (song == music.getCurrentPlaying())
				setSongLyrics(lyrics);
		} else {
			setSongLyrics(null);
		}
	}

	@UiThread
	void setSongLyrics(Lyric lyrics) {
		lblSongLyrics.setText(lyrics != null ? lyrics.toString() + "\n\n\n"
				+ lyrics.getLyric() : "");

		lyricsLoadingProgressBar.setVisibility(View.GONE);
	}

	private void playSong(Track song) {
		btnPlay.setVisibility(View.GONE);
		btnPause.setVisibility(View.VISIBLE);

		lblSongTitle.setText(song.getTitle());
		lblArtistName.setText(song.getArtist().getName());

		if (song != music.getCurrentPlaying()) {
			lblSongLyrics.setText("");
			lyricsLoadingProgressBar.setVisibility(View.GONE);
		}

		if (song != null) {
			music.play(song);
		}

		mHandler.postDelayed(mUpdateTimerRunnable, 100);
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
		boolean isPaused = music.isPaused();
		if (isShuffle) {
			int indexOfCurrent = shuffledSongsList.indexOf(music
					.getCurrentPlaying());
			if (indexOfCurrent < (shuffledSongsList.size() - 1)) {
				this.playSong(shuffledSongsList.get(indexOfCurrent + 1));
			} else {
				this.playSong(shuffledSongsList.get(0));
			}
		} else {
			int indexOfCurrent = songsList.indexOf(music.getCurrentPlaying());
			if (indexOfCurrent < (songsList.size() - 1)) {
				this.playSong(songsList.get(indexOfCurrent + 1));
			} else {
				this.playSong(songsList.get(0));
			}
		}

		if (isPaused) {
			this.pause();
		}
	}

	@Click(R.id.btnPrevious)
	void prev() {
		boolean isPaused = music.isPaused();

		if (isShuffle) {
			int indexOfCurrent = shuffledSongsList.indexOf(music
					.getCurrentPlaying());
			if (indexOfCurrent > 0) {
				this.playSong(shuffledSongsList.get(indexOfCurrent - 1));
			} else {
				this.playSong(shuffledSongsList.get(shuffledSongsList.size() - 1));
			}
		} else {
			int indexOfCurrent = songsList.indexOf(music.getCurrentPlaying());
			if (indexOfCurrent > 0) {
				this.playSong(songsList.get(indexOfCurrent - 1));
			} else {
				this.playSong(songsList.get(songsList.size() - 1));
			}
		}

		if (isPaused) {
			this.pause();
		}
	}

	/**
	 * Button Click event for Repeat button. Enables repeat flag to true if it
	 * is not set yet, and false when it already is. If set to true, shuffle is
	 * disabled
	 */
	@Click(R.id.btnRepeat)
	void toggleRepeat() {
		if (isRepeat) {
			this.setRepeat(false);
			Toast.makeText(getApplicationContext(), "Repeat is OFF",
					Toast.LENGTH_SHORT).show();
		} else {
			this.setRepeat(true);
			Toast.makeText(getApplicationContext(), "Repeat is ON",
					Toast.LENGTH_SHORT).show();
			// make shuffle ton false
			this.setShuffle(false);
		}
	}

	/**
	 * Button Click event for Shuffle button. Enables shuffle flag to true if it
	 * is not set yet, and false when it already is. If set to true, repeat is
	 * disabled
	 */
	@Click(R.id.btnShuffle)
	void toggleShuffle() {
		if (isShuffle) {
			this.setShuffle(false);
			Toast.makeText(getApplicationContext(), "Shuffle is OFF",
					Toast.LENGTH_SHORT).show();
		} else {
			this.setShuffle(true);
			Toast.makeText(getApplicationContext(), "Shuffle is ON",
					Toast.LENGTH_SHORT).show();
			// make repeat to false
			this.setRepeat(false);
		}
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;

		Editor editor = this.sharedPreferences.edit();
		editor.putBoolean(PREF_IS_REPEAT, this.isRepeat);
		editor.commit();

		if (this.isRepeat) {
			btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
		} else {
			btnRepeat.setImageResource(R.drawable.btn_repeat);
		}
		music.setLooping(this.isRepeat);
	}

	public void setShuffle(boolean isShuffle) {
		this.isShuffle = isShuffle;

		Editor editor = this.sharedPreferences.edit();
		editor.putBoolean(PREF_IS_SHUFFLE, this.isShuffle);
		editor.commit();

		if (this.isShuffle) {
			btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
			if (this.shuffledSongsList == null) {
				shuffledSongsList = new ArrayList<Track>(songsList);
				Collections.shuffle(shuffledSongsList);
			}
		} else {
			btnShuffle.setImageResource(R.drawable.btn_shuffle);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		music.dispose();
		mHandler.removeCallbacks(mUpdateTimerRunnable);
	}

	// Music progress-related methods!
	/**
	 * Update timer on seekbar
	 * */
	@Background
	public void updateProgressBar() {
		String totalDuration = timerUtils.milliSecondsToTimer(music
				.getMediaPlayer().getDuration());
		String currentDuration = timerUtils.milliSecondsToTimer(music
				.getMediaPlayer().getCurrentPosition());

		// Updating progress bar
		int progress = (int) (timerUtils.getProgressPercentage(music
				.getMediaPlayer().getCurrentPosition(), music.getMediaPlayer()
				.getDuration()));

		updateProgressBarViews(currentDuration, totalDuration, progress);

		// Running this thread after 100 milliseconds
		mHandler.postDelayed(mUpdateTimerRunnable, 100);
	}

	/**
	 * Update timer views (current position and total duration labels and the
	 * seek bar)
	 * */
	@UiThread
	public void updateProgressBarViews(String currentDuration,
			String totalDuration, int progress) {
		// Displaying Total Duration time
		lblSongTotalDuration.setText(totalDuration);
		// Displaying time completed playing
		lblSongCurrentDuration.setText(currentDuration);

		// Log.d("Progress", ""+progress);
		songProgressBar.setProgress(progress);
	}

	private Runnable mUpdateTimerRunnable = new Runnable() {
		@Override
		public void run() {
			updateProgressBar();
		}
	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimerRunnable);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimerRunnable);
		int totalDuration = music.getMediaPlayer().getDuration();
		int currentPosition = timerUtils.progressToTimer(
				songProgressBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		music.getMediaPlayer().seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}
}
