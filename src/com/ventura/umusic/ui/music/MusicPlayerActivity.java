package com.ventura.umusic.ui.music;

import java.util.List;

import org.apache.http.HttpException;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
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
import com.ventura.umusic.BaseApplication;
import com.ventura.umusic.R;
import com.ventura.umusic.business.LyricsService;
import com.ventura.umusic.entity.music.Audio;
import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.music.TracksManager;
import com.ventura.umusic.music.player.MusicPlayer;
import com.ventura.umusic.music.player.MusicPlayerListener;
import com.ventura.umusic.ui.BaseActivity;

@EActivity(R.layout.activity_music_player)
public class MusicPlayerActivity extends BaseActivity implements
		MusicPlayerListener, OnSeekBarChangeListener {
	private final String TAG = getClass().getName();

	public final String PREF_IS_SHUFFLE = getClass().getName()
			+ ".PREF_IS_SHUFFLE";
	public final String PREF_IS_REPEAT = getClass().getName()
			+ ".PREF_IS_REPEAT";
	public final String PREF_AUTOLOAD_LYRICS = getClass().getName()
			+ ".PREF_AUTOLOAD_LYRICS";

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

	@ViewById(R.id.btn_autoload_lyrics)
	protected ToggleButton btnAutoLoadLyrics;

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

	@ViewById(R.id.album_image)
	protected ImageView imgAlbumImage;

	@ViewById(R.id.album_mask)
	protected ImageView imgAlbumMask;

	@ViewById(R.id.loading_lyrics_progress_bar)
	ProgressBar pgbLyricsLoadingIndicator;

	private MusicPlayer musicPlayer;
	private TracksManager tracksManager;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracksManager = new TracksManager(this);
		decipherIntent();
	}

	@AfterViews
	protected void afterViews() {
		skbSongProgress.setOnSeekBarChangeListener(this);

		loadPlayer();
	}

	private void decipherIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();

		if (Intent.ACTION_MAIN.equals(action))
			return;
	}

	private void loadPlayer() {
		musicPlayer = new MusicPlayer(this);
		musicPlayer.setListener(this);

		boolean isRepeat = getApplicationPreferences().getBoolean(
				BaseApplication.Preferences.MP_IS_REPEAT, false);
		boolean isShuffle = getApplicationPreferences().getBoolean(
				BaseApplication.Preferences.MP_IS_SHUFFLE, false);
		boolean autoLoadLyrics = getApplicationPreferences().getBoolean(
				BaseApplication.Preferences.MP_AUTOLOAD_LYRICS, false);

		setRepeat(isRepeat);
		setShuffle(isShuffle);
		setAutoLoadLyrics(autoLoadLyrics);

		TimerUtils timerUtils = new TimerUtils();
		lblCurrentPosition.setText(timerUtils.milliSecondsToTimer(0));
		lblTotalDuration.setText(timerUtils.milliSecondsToTimer(0));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		musicPlayer.dispose();
	}

	/**
	 * Refreshes track name, album, etc
	 */
	private void refreshTrackInfoView() {
		Audio currentPlaying = musicPlayer.getCurrentPlaying();
		if (currentPlaying != null) {
			lblSongTitle.setText(currentPlaying.getTitle());
			lblAlbum.setText(currentPlaying.getAlbumTitle());
			lblArtist.setText(currentPlaying.getArtistName());

			tracksManager.loadAlbum(currentPlaying);

			if (currentPlaying.getAlbumImage() == null) {
				// if null, the album art is hidden
				imgAlbumImage.setImageBitmap(null);
			} else {
				imgAlbumImage.setImageBitmap(currentPlaying.getAlbumImage());
			}

			// if user asked to auto load lyrics, do it
			if (getApplicationPreferences().getBoolean(
					BaseApplication.Preferences.MP_AUTOLOAD_LYRICS, false)) {
				getCurrentPlayingLyrics();
			}
		}
	}
	
	void getCurrentPlayingLyrics(){
		Audio currentPlaying = musicPlayer.getCurrentPlaying();
		if (currentPlaying != null && lblLyrics.getText().equals("")) {
			pgbLyricsLoadingIndicator.setVisibility(View.VISIBLE);
			findLyrics(currentPlaying.getArtistName(),
					currentPlaying.getTitle());
		}
	}

	@Background
	void findLyrics(String artist, String song) {
		Lyrics l = null;
		try {
			l = new LyricsService(this).getLyrics(artist, song);
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
			alert(R.string.message_no_internet_connection);
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
			alert(R.string.message_lazy_internet_connection);
		} catch (HttpException e) {
			e.printStackTrace();
			alert("ERROR!", e.getMessage());
		}

		setLyrics(artist, song, l);
	}

	@UiThread
	void setLyrics(String searchedArtist, String searchedSong, Lyrics l) {
		boolean isFromCurrentTrack = searchedArtist.equals(lblArtist.getText())
				&& searchedSong.equals(lblSongTitle.getText());
		boolean isAutoLoadingLyrics = getApplicationPreferences().getBoolean(
				BaseApplication.Preferences.MP_AUTOLOAD_LYRICS, false);

		if (l == null) {
			lblLyrics.setText(null);
			if (isFromCurrentTrack && !isAutoLoadingLyrics)
				alert(R.string.message_lyric_not_found);
		} else if (isFromCurrentTrack)
			lblLyrics.setText(l.getLyricsText());

		if (isFromCurrentTrack)
			pgbLyricsLoadingIndicator.setVisibility(View.INVISIBLE);
	}

	private void clearLyrics() {
		lblLyrics.setText(null);
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
	}

	@Click(R.id.btn_backward)
	public void prev() {
		musicPlayer.prev();
	}

	@Click(R.id.btn_stop)
	public void stop() {
		musicPlayer.stop();
		refreshTrackInfoView();
	}

	@Click(R.id.btn_shuffle)
	public void toggleShuffle() {
		musicPlayer.toggleShuffle();

		Editor editor = getApplicationPreferences().edit();
		editor.putBoolean(BaseApplication.Preferences.MP_IS_SHUFFLE,
				musicPlayer.isShuffle());
		editor.apply();
	}

	protected void setShuffle(boolean shuffle) {
		musicPlayer.setShuffle(shuffle);
		btnShuffle.setChecked(musicPlayer.isShuffle());
	}

	@Click(R.id.btn_repeat)
	public void toggleRepeat() {
		musicPlayer.toggleRepeat();

		Editor editor = getApplicationPreferences().edit();
		editor.putBoolean(BaseApplication.Preferences.MP_IS_REPEAT,
				musicPlayer.isRepeat());
		editor.apply();

	}

	protected void setRepeat(boolean repeat) {
		musicPlayer.setRepeat(repeat);
		btnRepeat.setChecked(musicPlayer.isRepeat());
	}

	@Click(R.id.btn_autoload_lyrics)
	public void toggleAutoLoadLyrics() {
		boolean autoLoad = getApplicationPreferences().getBoolean(
				BaseApplication.Preferences.MP_AUTOLOAD_LYRICS, false);
		Editor editor = getApplicationPreferences().edit();
		editor.putBoolean(BaseApplication.Preferences.MP_AUTOLOAD_LYRICS,
				!autoLoad);
		editor.apply();

		setAutoLoadLyrics(!autoLoad);
	}

	private void setAutoLoadLyrics(boolean autoLoad) {
		btnAutoLoadLyrics.setChecked(autoLoad);
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
	public void onMusicChanged(Audio oldSong, Audio newSong) {
		clearLyrics();
		refreshTrackInfoView();
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