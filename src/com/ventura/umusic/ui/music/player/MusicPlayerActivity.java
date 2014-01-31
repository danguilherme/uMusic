package com.ventura.umusic.ui.music.player;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.ventura.umusic.music.player.MusicPlayerService;
import com.ventura.umusic.music.player.MusicPlayerServiceInterface;
import com.ventura.umusic.ui.BaseActivity;

@EActivity(R.layout.activity_music_player)
public class MusicPlayerActivity extends BaseActivity implements
		OnSeekBarChangeListener {
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

	@ViewById(R.id.btn_open_playlist)
	ImageButton btnOpenPlaylist;

	// private MusicPlayer musicPlayer;
	private TracksManager tracksManager;
	private Audio mCurrentPlaying;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();

	private MusicPlayerServiceInterface mpInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tracksManager = new TracksManager(this);
		decipherIntent();

		if (MusicPlayerService.isRunnning()) {
			MusicPlayerActivity.this.bindService(new Intent(
					MusicPlayerActivity.this, MusicPlayerService.class),
					mConnection, Context.BIND_AUTO_CREATE);
		} else {
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					MusicPlayerActivity.this.bindService(
							new Intent(MusicPlayerActivity.this,
									MusicPlayerService.class), mConnection,
							Context.BIND_AUTO_CREATE);

					MusicPlayerActivity.this.unregisterReceiver(this);
				}
			}, new IntentFilter(MusicPlayerService.ACTION_STARTED));

			startService(new Intent(MusicPlayerActivity.this,
					MusicPlayerService.class));
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(MusicPlayerService.ACTION_MUSIC_CHANGED);
		registerReceiver(musicPlayerReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(musicPlayerReceiver);
		unbindService(mConnection);
	}

	BroadcastReceiver musicPlayerReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MusicPlayerService.ACTION_MUSIC_CHANGED.equals(intent
					.getAction())) {
				onMusicChanged(intent.getStringExtra(Audio.KEY_URI));
			}
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mpInterface = MusicPlayerServiceInterface.Stub
					.asInterface((IBinder) service);
			try {
				List<String> actualPlaylist = mpInterface.getPlaylist();
				if (actualPlaylist == null) {
					List<String> playlist = tracksManager
							.getAllTracksSimplified();
					if (playlist.size() == 0) {
						alert("No music found");
						finish();
					} else
						mpInterface.setPlaylist(playlist);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			loadPlayer();
			updateTrackInfoView();
		}

		public void onServiceDisconnected(ComponentName className) {
			mpInterface = null;
		}
	};

	@AfterViews
	protected void afterViews() {
		skbSongProgress.setOnSeekBarChangeListener(this);
	}

	private void decipherIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();

		if (Intent.ACTION_MAIN.equals(action))
			return;
	}

	private void loadPlayer() {
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

	/**
	 * Refreshes track name, album, etc
	 */
	private void updateTrackInfoView() {
		try {
			mCurrentPlaying = tracksManager.getTrackByUri(mpInterface
					.getCurrentPlaying());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (mCurrentPlaying != null) {
			lblSongTitle.setText(mCurrentPlaying.getTitle());
			lblAlbum.setText(mCurrentPlaying.getAlbumTitle());
			lblArtist.setText(mCurrentPlaying.getArtistName());

			tracksManager.loadAlbum(mCurrentPlaying);

			if (mCurrentPlaying.getAlbumImage() == null) {
				// if null, the album art is hidden
				imgAlbumImage.setImageBitmap(null);
			} else {
				imgAlbumImage.setImageBitmap(mCurrentPlaying.getAlbumImage());
			}

			// if user asked to auto load lyrics, do it
			if (getApplicationPreferences().getBoolean(
					BaseApplication.Preferences.MP_AUTOLOAD_LYRICS, false)) {
				getCurrentPlayingLyrics();
			}
		}

		updateProgressBarViews(); // make sure the progress bar is refreshed too
	}

	void getCurrentPlayingLyrics() {
		if (mCurrentPlaying != null && lblLyrics.getText().equals("")) {
			pgbLyricsLoadingIndicator.setVisibility(View.VISIBLE);
			findLyrics(mCurrentPlaying.getArtistName(),
					mCurrentPlaying.getTitle());
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
		} else if (isFromCurrentTrack) {
			lblLyrics.setText(l.getLyricsText());
			mCurrentPlaying.setLyrics(l.getLyricsText());
		}

		if (isFromCurrentTrack)
			pgbLyricsLoadingIndicator.setVisibility(View.INVISIBLE);
	}

	private void clearLyrics() {
		lblLyrics.setText(null);
	}

	@Click(R.id.btn_play)
	public void play() {
		try {
			if (mpInterface.isPaused())
				mpInterface.play(null);

			mHandler.postDelayed(mUpdateTimerRunnable, 100);

			updateTrackInfoView();

			btnPause.setVisibility(View.VISIBLE);
			btnPlay.setVisibility(View.GONE);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.btn_pause)
	public void pause() {
		try {
			mpInterface.pause();

			btnPlay.setVisibility(View.VISIBLE);
			btnPause.setVisibility(View.GONE);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.btn_forward)
	public void next() {
		try {
			mpInterface.next();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.btn_backward)
	public void prev() {
		try {
			mpInterface.prev();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.btn_stop)
	public void stop() {
		try {
			mpInterface.stop();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		updateTrackInfoView();
	}

	@Click(R.id.btn_shuffle)
	public void toggleShuffle() {
		try {
			boolean isShuffle = mpInterface.toggleShuffle();

			Editor editor = getApplicationPreferences().edit();
			editor.putBoolean(BaseApplication.Preferences.MP_IS_SHUFFLE,
					isShuffle);
			editor.apply();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void setShuffle(boolean shuffle) {
		try {
			mpInterface.setShuffle(shuffle);
			btnShuffle.setChecked(mpInterface.isShuffle());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Click(R.id.btn_repeat)
	public void toggleRepeat() {
		try {
			boolean isRepeat = mpInterface.toggleRepeat();

			Editor editor = getApplicationPreferences().edit();
			editor.putBoolean(BaseApplication.Preferences.MP_IS_REPEAT,
					isRepeat);
			editor.apply();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void setRepeat(boolean repeat) {
		try {
			mpInterface.setRepeat(repeat);
			btnRepeat.setChecked(mpInterface.isRepeat());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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

	@Click(R.id.btn_open_playlist)
	protected void openPlaylist() {
		try {
			Intent intent = new Intent(PlaylistActivity.ACTION_CHOOSE_SONG);
			intent.putStringArrayListExtra(
					PlaylistActivity.EXTRA_PLAYLIST_ARRAY,
					new ArrayList<String>(mpInterface.getPlaylist()));
			intent.putExtra(PlaylistActivity.EXTRA_PLAYING, tracksManager
					.getTrackByUri(mpInterface.getCurrentPlaying())
					.getPathUri().toString());
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
		int currentPosition = 0;
		int duration = 0;
		try {
			currentPosition = mpInterface.getCurrentDuration();
			duration = mpInterface.getDuration();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

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
	public void onMusicChanged(String newSong) {
		clearLyrics();
		updateTrackInfoView();
	}

	// Seekbar listeners
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {
		if (fromTouch) {
			try {
				int totalDuration = mpInterface.getDuration();
				int changedPosition = new TimerUtils().progressToTimer(
						skbSongProgress.getProgress(), totalDuration);

				mpInterface.seekTo(changedPosition);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
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