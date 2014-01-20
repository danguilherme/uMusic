package com.ventura.umusic.ui.music;

import java.util.List;

import org.apache.http.HttpException;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.androidutils.utils.TimerUtils;
import com.ventura.umusic.R;
import com.ventura.umusic.business.LyricsService;
import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.entity.music.Track;
import com.ventura.umusic.music.TracksManager;
import com.ventura.umusic.music.player.MusicPlayer;
import com.ventura.umusic.music.player.MusicPlayerListener;
import com.ventura.umusic.ui.BaseActivity;

@EActivity(R.layout.activity_music_player)
public class MusicPlayerActivity extends BaseActivity implements
		MusicPlayerListener, OnSeekBarChangeListener {
	private final String TAG = getClass().getName();

	@ViewById(R.id.btn_play)
	protected Button btnPlay;

	@ViewById(R.id.btn_pause)
	protected Button btnPause;

	@ViewById(R.id.btn_stop)
	protected Button btnStop;

	@ViewById(R.id.btn_forward)
	protected Button btnForward;

	@ViewById(R.id.btn_backward)
	protected Button btnBackward;

	@ViewById(R.id.btn_shuffle)
	protected ToggleButton btnShuffle;

	@ViewById(R.id.btn_repeat)
	protected ToggleButton btnRepeat;

	@ViewById(R.id.lbl_music_title)
	protected TextView lblSongTitle;

	@ViewById(R.id.lbl_artist_name)
	protected TextView lblArtist;

	@ViewById(R.id.lbl_music_album)
	protected TextView lblAlbum;

	@ViewById(R.id.skb_song_progress)
	protected SeekBar skbSongProgress;

	@ViewById(R.id.lbl_current_position)
	protected TextView lblCurrentPosition;

	@ViewById(R.id.lbl_total_duration)
	protected TextView lblTotalDuration;

	@ViewById(R.id.lbl_lyrics)
	protected TextView lblLyrics;

	private MusicPlayer musicPlayer;
	private TracksManager tracksManager;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		musicPlayer = new MusicPlayer(getApplicationContext());
		musicPlayer.setListener(this);
		tracksManager = new TracksManager(this);
		decipherIntent();
	}

	@AfterViews
	protected void afterViews() {
		skbSongProgress.setOnSeekBarChangeListener(this);
		lblLyrics.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lblLyrics.getText() == "")
					findLyrics(lblArtist.getText().toString(), lblSongTitle
							.getText().toString());
			}
		});
	}

	private void decipherIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();

		if (Intent.ACTION_MAIN.equals(action))
			return;
	}

	/**
	 * Refreshes track name, album, etc
	 */
	private void refreshTrackInfoView() {
		Track currentPlaying = musicPlayer.getCurrentPlaying();
		if (currentPlaying != null) {
			lblSongTitle.setText(currentPlaying.getTitle());
			lblAlbum.setText(currentPlaying.getAlbumTitle());
			lblArtist.setText(currentPlaying.getArtistName());
		}
	}

	@Background
	void findLyrics(String artist, String song) {
		Lyrics l = null;
		try {
			l = new LyricsService(this).getLyrics(artist, song);
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		}

		setLyrics(l);
	}

	@UiThread
	void setLyrics(Lyrics l) {
		if (l == null) {
			lblLyrics.setText(null);
		} else
			lblLyrics.setText(l.getLyricsText());
	}

	@Click(R.id.btn_play)
	public void play() {
		List<String> pl = musicPlayer.getPlaylist();

		if (pl == null) {
			musicPlayer.setPlaylist(tracksManager.getAllTracksSimplified());
		}

		if (musicPlayer.isPaused())
			musicPlayer.play();

		mHandler.postDelayed(mUpdateTimerRunnable, 100);

		refreshTrackInfoView();
	}

	@Click(R.id.btn_pause)
	public void pause() {
		if (musicPlayer.isPaused())
			musicPlayer.play();
		else
			musicPlayer.pause();
	}

	@Click(R.id.btn_forward)
	public void next() {
		musicPlayer.next();
		refreshTrackInfoView();
	}

	@Click(R.id.btn_backward)
	public void prev() {
		musicPlayer.prev();
		refreshTrackInfoView();
	}

	@Click(R.id.btn_stop)
	public void stop() {
		musicPlayer.stop();
		refreshTrackInfoView();
	}

	@Click(R.id.btn_shuffle)
	public void toggleShuffle() {
		musicPlayer.toggleShuffle();
		btnShuffle.setChecked(musicPlayer.isShuffle());
		btnRepeat.setChecked(musicPlayer.isRepeat());
	}

	@Click(R.id.btn_repeat)
	public void toggleRepeat() {
		musicPlayer.toggleRepeat();
		btnRepeat.setChecked(musicPlayer.isRepeat());
		btnShuffle.setChecked(musicPlayer.isShuffle());
	}

	/**
	 * Runs at background to update progress bar view.
	 */
	private Runnable mUpdateTimerRunnable = new Runnable() {
		@Override
		public void run() {
			updateProgressBarViews();
		}
	};

	/**
	 * Update timer views (current position and total duration labels) and the
	 * seek bar
	 * */
	@UiThread
	public void updateProgressBarViews() {
		TimerUtils timerUtils = new TimerUtils();
		int currentPosition = musicPlayer.getMediaPlayer().getCurrentPosition();
		int duration = musicPlayer.getMediaPlayer().getDuration();

		int progress = timerUtils.getProgressPercentage(currentPosition,
				duration);
		lblCurrentPosition.setText(timerUtils
				.milliSecondsToTimer(currentPosition));
		lblTotalDuration.setText(timerUtils.milliSecondsToTimer(duration));

		skbSongProgress.setProgress(progress);

		// Running this thread after 100 milliseconds
		mHandler.postDelayed(mUpdateTimerRunnable, 100);
	}

	// LISTENERS

	// Music player listeners
	@Override
	public void onMusicChanged(Track oldSong, Track newSong) {
		refreshTrackInfoView();
		setLyrics(null);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
	}

	// Seekbar listeners
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		if (fromTouch) {
			int totalDuration = musicPlayer.getMediaPlayer().getDuration();
			int changedPosition = new TimerUtils().progressToTimer(
					skbSongProgress.getProgress(), totalDuration);

			musicPlayer.seekTo(changedPosition);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimerRunnable);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// update timer progress again
		updateProgressBarViews();

	}
}