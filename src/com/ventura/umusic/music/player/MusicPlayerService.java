package com.ventura.umusic.music.player;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import com.ventura.umusic.R;
import com.ventura.umusic.entity.music.Audio;
import com.ventura.umusic.ui.music.player.MusicPlayerActivity_;

public class MusicPlayerService extends Service implements
		MusicPlayer.MusicPlayerListener {
	private final String TAG = getClass().getName();

	// Sent actions
	public static final String ACTION_STARTED = "com.ventura.umusic.music.player.MusicPlayerService.STARTED";
	public static final String ACTION_MUSIC_CHANGED = "com.ventura.umusic.music.player.MusicPlayerService.MUSIC_CHANGED";

	// Received actions
	public static final String ACTION_PLAY = "com.ventura.umusic.music.player.MusicPlayerService.PLAY";
	public static final String ACTION_PAUSE = "com.ventura.umusic.music.player.MusicPlayerService.PAUSE";
	public static final String ACTION_NEXT = "com.ventura.umusic.music.player.MusicPlayerService.NEXT";
	public static final String ACTION_PREVIOUS = "com.ventura.umusic.music.player.MusicPlayerService.PREVIOUS";

	private MusicPlayer mp;
	private static boolean isRunning = false;

	private NotificationManager nm;
	private static final int NOTIFY_ID = 001;

	private AudioManager mAudioManager;
	private ComponentName mediaButtonReceiver;

	public static boolean isRunnning(){
		return isRunning;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!isRunning) {
			Intent i = new Intent(MusicPlayerService.ACTION_STARTED);
			sendBroadcast(i);
			isRunning = true;
		}

		String action = intent.getAction();
		if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
			pause();
		} else if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
			KeyEvent event = (KeyEvent) intent
					.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			handleMediaButtonPress(event);
		} else if (ACTION_PLAY.equals(action)) {
			play(intent.getStringExtra(Audio.KEY_URI));
		} else if (ACTION_PAUSE.equals(action)) {
			pause();
		} else if (ACTION_NEXT.equals(action)) {
			next();
		} else if (ACTION_PREVIOUS.equals(action)) {
			prev();
		}

		return Service.START_NOT_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mp = new MusicPlayer(this);
		mp.setListener(this);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	private void requestAudioFocus() {
		// Request audio focus for playback
		int result = mAudioManager.requestAudioFocus(audioFocusChangeListener,
		// Use the music stream.
				AudioManager.STREAM_MUSIC,
				// Request permanent focus.
				AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			mediaButtonReceiver = new ComponentName(getPackageName(),
					MusicPlayerBroadcastReceiver.class.getCanonicalName());
			mAudioManager.registerMediaButtonEventReceiver(mediaButtonReceiver);
		}
	}

	OnAudioFocusChangeListener audioFocusChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				pause();
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				play();
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				mAudioManager
						.unregisterMediaButtonEventReceiver(mediaButtonReceiver);
				mAudioManager.abandonAudioFocus(audioFocusChangeListener);
				// Stop playback
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "destroyed");
		isRunning = false;
		mp.dispose();
		nm.cancel(NOTIFY_ID);
		mAudioManager.unregisterMediaButtonEventReceiver(mediaButtonReceiver);
		mAudioManager.abandonAudioFocus(audioFocusChangeListener);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		setNotification();
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		nm.cancel(NOTIFY_ID);
		return mBinder;
	}

	private void setNotification() {
		Audio current = mp.getCurrentPlaying();
		Notification.Builder mBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				// .setOngoing(true)
				// .addAction(
				// R.drawable.play,
				// "Play",
				// PendingIntent.getService(this, NOTIFY_ID, new Intent(
				// this, MusicPlayerService.class), 0))
				.setContentTitle(current.getTitle())
				.setContentText(
						current.getArtistName() + " - "
								+ current.getAlbumTitle())
				.setContentIntent(
						PendingIntent.getActivity(this, 2, new Intent(this,
								MusicPlayerActivity_.class),
								PendingIntent.FLAG_UPDATE_CURRENT));
		nm.notify(NOTIFY_ID, mBuilder.build());
	}

	@Override
	public void onMusicChanged(Audio oldSong, Audio newSong) {
		setNotification();

		Intent intent = new Intent(MusicPlayerService.ACTION_MUSIC_CHANGED);
		intent.putExtra(Audio.KEY_URI, newSong.getPathUri().toString());
		sendBroadcast(intent);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
	}

	// Media button handling

	private int mediaButtonPressCount;
	Handler mediaButtonVerifierHandler = new Handler();

	private void registerMediaButtonPress() {
		synchronized (this) {
			mediaButtonPressCount++;
		}
	}

	private void performMediaButtonAction() {
		Log.d(TAG, "Media button presses: " + mediaButtonPressCount);
		if (mediaButtonPressCount == 1)
			togglePlayPause();
		else if (mediaButtonPressCount == 2)
			next();
		else if (mediaButtonPressCount >= 3)
			prev();

		synchronized (this) {
			mediaButtonPressCount = 0;
		}
		mediaButtonVerifierHandler
				.removeCallbacks(performMediaButtonActionRunnable);
	}

	Runnable performMediaButtonActionRunnable = new Runnable() {
		@Override
		public void run() {
			performMediaButtonAction();
		}
	};

	long prevEventTime = 0;

	private void handleMediaButtonPress(KeyEvent event) {
		Log.d(TAG, "handleMediaButtonPress");
		long currentEvTime = event.getEventTime();
		if (currentEvTime == prevEventTime) {
			Log.d(TAG, "Repeated event, cancelling...");
			return;
		} else {
			prevEventTime = currentEvTime;
		}

		if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_MEDIA_PLAY");
		} else if (KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_MEDIA_NEXT");
		} else if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_MEDIA_PREVIOUS");
		} else if (KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_MEDIA_STOP");
		} else if (KeyEvent.KEYCODE_MEDIA_PAUSE == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_MEDIA_PAUSE");
		} else if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_MEDIA_PLAY_PAUSE");
		} else if (KeyEvent.KEYCODE_HEADSETHOOK == event.getKeyCode()) {
			Log.d(TAG, "KEYCODE_HEADSETHOOK");
		}

		registerMediaButtonPress();
		mediaButtonVerifierHandler
				.removeCallbacks(performMediaButtonActionRunnable);
		mediaButtonVerifierHandler.postDelayed(
				performMediaButtonActionRunnable, 500);
	}

	// Media player functions

	private void togglePlayPause() {
		if (mp.isPaused())
			play();
		else
			pause();
	}

	private void play() {
		play(null);
	}

	private void play(String song) {
		if (song != null)
			mp.play(song);
		else
			mp.play();
		
		requestAudioFocus();
	}

	private void pause() {
		mp.pause();
		mAudioManager.abandonAudioFocus(audioFocusChangeListener);
	}

	protected void next() {
		mp.next();
	}

	protected void prev() {
		mp.prev();
	}

	private final MusicPlayerServiceInterface.Stub mBinder = new MusicPlayerServiceInterface.Stub() {

		@Override
		public void stop() throws RemoteException {
			mp.stop();
		}

		@Override
		public void setPlaylist(List<String> newPlaylist)
				throws RemoteException {
			mp.setPlaylist(newPlaylist);
		}

		@Override
		public List<String> getPlaylist() throws RemoteException {
			return mp.getPlaylist();
		}

		@Override
		public void play(String songPath) throws RemoteException {
			MusicPlayerService.this.play(songPath);
		}

		@Override
		public void pause() throws RemoteException {
			MusicPlayerService.this.pause();
		}

		@Override
		public void next() throws RemoteException {
			MusicPlayerService.this.next();
		}

		@Override
		public void prev() throws RemoteException {
			MusicPlayerService.this.prev();
		}

		@Override
		public void seekTo(int position) throws RemoteException {
			mp.seekTo(position);
		}

		@Override
		public int getPid() throws RemoteException {
			return 0;
		}

		@Override
		public void clearPlaylist() throws RemoteException {
			mp.setPlaylist(new ArrayList<String>());
		}

		@Override
		public boolean isPaused() throws RemoteException {
			return mp.isPaused();
		}

		@Override
		public boolean isShuffle() throws RemoteException {
			return mp.isShuffle();
		}

		@Override
		public void setShuffle(boolean shuffle) throws RemoteException {
			mp.setShuffle(shuffle);
		}

		@Override
		public boolean toggleShuffle() throws RemoteException {
			mp.toggleShuffle();
			return mp.isShuffle();
		}

		@Override
		public boolean isRepeat() throws RemoteException {
			return mp.isRepeat();
		}

		@Override
		public void setRepeat(boolean repeat) throws RemoteException {
			mp.setRepeat(repeat);
		}

		@Override
		public boolean toggleRepeat() throws RemoteException {
			mp.toggleRepeat();
			return mp.isRepeat();
		}

		@Override
		public String getCurrentPlaying() throws RemoteException {
			return mp.getNowPlaying();
		}

		@Override
		public int getDuration() throws RemoteException {
			return mp.getMediaPlayer().getDuration();
		}

		@Override
		public int getCurrentDuration() throws RemoteException {
			return mp.getMediaPlayer().getCurrentPosition();
		}
	};
}
