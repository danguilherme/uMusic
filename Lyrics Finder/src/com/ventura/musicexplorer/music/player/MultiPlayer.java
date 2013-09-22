package com.ventura.musicexplorer.music.player;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

/**
 * Provides a unified interface for dealing with midi files and other media
 * files.
 */
public class MultiPlayer {
	private MediaPlayerCompat mCurrentMediaPlayer = new MediaPlayerCompat();
	private MediaPlayerCompat mNextMediaPlayer;
	private MediaPlayerHandler mHandler;
	
	private boolean mIsInitialized = false;
	private boolean mIsPlaying = false;

	public static final int MESSAGE_TRACK_WENT_TO_NEXT = 0;
	public static final int MESSAGE_TRACK_ENDED = 1;
	public static final int MESSAGE_RELEASE_WAKELOCK = 2;
	public static final int MESSAGE_SERVER_DIED = 3;

	private Context mContext;

	public MultiPlayer(Context context) {
		this.mContext = context;
		mCurrentMediaPlayer.setWakeMode(mContext,
				PowerManager.PARTIAL_WAKE_LOCK);
	}

	public void setDataSource(String path) {
		mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
		if (mIsInitialized) {
			setNextDataSource(null);
		}
	}

	private boolean setDataSourceImpl(MediaPlayer player, String path) {
		try {
			player.reset();
			player.setOnPreparedListener(null);
			if (path.startsWith("content://")) {
				player.setDataSource(mContext, Uri.parse(path));
			} else {
				player.setDataSource(path);
			}
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.prepare();
		} catch (IOException ex) {
			// TODO: notify the user why the file couldn't be opened
			return false;
		} catch (IllegalArgumentException ex) {
			// TODO: notify the user why the file couldn't be opened
			return false;
		}
		player.setOnCompletionListener(listener);
		player.setOnErrorListener(errorListener);

		return true;
	}

	@SuppressLint("NewApi")
	public void setNextDataSource(String path) {
		mCurrentMediaPlayer.setNextMediaPlayer(null);
		if (mNextMediaPlayer != null) {
			mNextMediaPlayer.release();
			mNextMediaPlayer = null;
		}
		if (path == null) {
			return;
		}
		mNextMediaPlayer = new MediaPlayerCompat();
		mNextMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);

		if (setDataSourceImpl(mNextMediaPlayer, path)) {
			mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
		} else {
			// failed to open next, we'll transition the old fashioned way,
			// which will skip over the faulty file
			mNextMediaPlayer.release();
			mNextMediaPlayer = null;
		}
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	/** {@inheritDoc MediaPlayer#start()} */
	public void start() {
		mIsPlaying = true;
		mCurrentMediaPlayer.start();
	}

	public void stop() {
		mCurrentMediaPlayer.reset();
		mIsPlaying = false;
		mIsInitialized = false;
	}

	/**
	 * You CANNOT use this player anymore after calling release()
	 */
	public void release() {
		stop();
		mCurrentMediaPlayer.release();
	}

	public void pause() {
		mCurrentMediaPlayer.pause();
		mIsPlaying = false;
	}

	public void setHandler(MediaPlayerHandler handler) {
		mHandler = handler;
	}

	MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
				mCurrentMediaPlayer.release();
				mCurrentMediaPlayer = mNextMediaPlayer;
				mNextMediaPlayer = null;
				mHandler.sendEmptyMessage(MESSAGE_TRACK_WENT_TO_NEXT);
			} else {
				// Acquire a temporary wakelock, since when we return from
				// this callback the MediaPlayer will release its wakelock
				// and allow the device to go to sleep.
				// This temporary wakelock is released when the
				// RELEASE_WAKELOCK
				// message is processed, but just in case, put a timeout on
				// it.
				// TODO: review this
				// mWakeLock.acquire(30000);
				mHandler.sendEmptyMessage(MESSAGE_TRACK_ENDED);
				mHandler.sendEmptyMessage(MESSAGE_RELEASE_WAKELOCK);
			}
		}
	};

	MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				mIsInitialized = false;
				mCurrentMediaPlayer.release();
				// Creating a new MediaPlayer and settings its wakemode does
				// not
				// require the media service, so it's OK to do this now,
				// while the
				// service is still being restarted
				mCurrentMediaPlayer = new MediaPlayerCompat();
				mCurrentMediaPlayer.setWakeMode(mContext,
						PowerManager.PARTIAL_WAKE_LOCK);
				mHandler.sendMessageDelayed(
						mHandler.obtainMessage(MESSAGE_SERVER_DIED), 2000);
				return true;
			default:
				Log.d("MultiPlayer", "Error: " + what + "," + extra);
				break;
			}
			return false;
		}
	};

	public long getDuration() {
		return mCurrentMediaPlayer.getDuration();
	}

	public long getCurrentPosition() {
		return mCurrentMediaPlayer.getCurrentPosition();
	}
	
	public boolean isPlaying(){
		return mCurrentMediaPlayer.isPlaying();
	}

 	public long seek(long whereto) {
		mCurrentMediaPlayer.seekTo((int) whereto);
		return whereto;
	}

	public void setVolume(float vol) {
		mCurrentMediaPlayer.setVolume(vol, vol);
	}

	@SuppressLint("NewApi")
	private static class MediaPlayerCompat extends MediaPlayer implements
			OnCompletionListener {

		private boolean mCompatMode = true;
		private MediaPlayer mNextPlayer;
		private OnCompletionListener mCompletion;

		public MediaPlayerCompat() {
			try {
				MediaPlayer.class.getMethod("setNextMediaPlayer",
						MediaPlayer.class);
				mCompatMode = false;
			} catch (NoSuchMethodException e) {
				mCompatMode = true;
				super.setOnCompletionListener(this);
			}
		}

		public void setNextMediaPlayer(MediaPlayer next) {
			if (mCompatMode) {
				mNextPlayer = next;
			} else {
				super.setNextMediaPlayer(next);
			}
		}

		@Override
		public void setOnCompletionListener(OnCompletionListener listener) {
			if (mCompatMode) {
				mCompletion = listener;
			} else {
				super.setOnCompletionListener(listener);
			}
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			if (mNextPlayer != null) {
				// as it turns out, starting a new MediaPlayer on the completion
				// of a previous player ends up slightly overlapping the two
				// playbacks, so slightly delaying the start of the next player
				// gives a better user experience
				SystemClock.sleep(50);
				mNextPlayer.start();
			}
			if (mCompletion != null) {
				mCompletion.onCompletion(this);	
			}
		}
	}
}