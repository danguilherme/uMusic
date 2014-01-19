package com.ventura.umusic.music;

import java.io.IOException;

import com.ventura.umusic.entity.music.Track;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class Music implements OnCompletionListener {
	final String TAG = getClass().getName();

	private MediaPlayer mediaPlayer;
	private boolean isPrepared = false;
	private boolean isPaused = false;

	private OnCompletionListener onCompletionListener;

	private Track currentPlaying;
	private Context context;

	public Music(Context context) {
		this.context = context;
		this.initializeMediaPlayer();
	}

	private void initializeMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		currentPlaying = null;
	}

	public void onCompletion(MediaPlayer mediaPlayer) {
		synchronized (this) {
			isPrepared = false;
		}
		if (onCompletionListener != null) {
			onCompletionListener.onCompletion(mediaPlayer);
		}
	}

	private void prepare() {
		try {
			mediaPlayer.prepare();
			isPrepared = true;
		} catch (IllegalStateException e) {
			Log.e(TAG, "Error when preparing Media Player");
		} catch (IOException e) {
			Log.e(TAG, "Error when preparing Media Player");
		}
	}

	/**
	 * Plays a song. Of this song is already playing, ignores it.
	 * 
	 * @param song
	 *            The song to play.
	 */
	public void play(Track song) {
		try {
			synchronized (this) {
				if (currentPlaying != null && currentPlaying.equals(song)) {
					if (isPaused)
						mediaPlayer.start();
				} else {
					stop();
					mediaPlayer.reset();
					mediaPlayer.setDataSource(context, song.getPathUri());
					if (!isPrepared) {
						prepare();
					}
					mediaPlayer.start();
					currentPlaying = song;
				}
				isPaused = false;
			}
		} catch (Exception ex) {
			this.initializeMediaPlayer();
			throw new RuntimeException("Couldn't load music, uh oh!");
		}
	}

	public void stop() {
		mediaPlayer.stop();
		synchronized (this) {
			isPrepared = false;
		}
	}

	public void switchTracks() {
		mediaPlayer.seekTo(0);
		mediaPlayer.pause();
	}

	public void pause() {
		mediaPlayer.pause();
		this.isPaused = true;
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public boolean isLooping() {
		return mediaPlayer.isLooping();
	}

	public void setLooping(boolean isLooping) {
		mediaPlayer.setLooping(isLooping);
	}

	public void setVolume(float volumeLeft, float volumeRight) {
		mediaPlayer.setVolume(volumeLeft, volumeRight);
	}

	public void dispose() {
		if (mediaPlayer.isPlaying()) {
			stop();
		}
		mediaPlayer.release();
	}

	public Track getCurrentPlaying() {
		return currentPlaying;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public OnCompletionListener getOnCompletionListener() {
		return onCompletionListener;
	}

	public void setOnCompletionListener(
			OnCompletionListener onCompletionListener) {
		this.onCompletionListener = onCompletionListener;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}