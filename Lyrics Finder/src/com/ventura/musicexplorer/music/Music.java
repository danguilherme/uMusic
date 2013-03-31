package com.ventura.musicexplorer.music;

import java.io.IOException;

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

	private Song currentPlaying;
	private Context context;

	public Music(Context context) {
		this.context = context;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
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
	public void play(Song song) {
		if (currentPlaying != null && currentPlaying.equals(song)) {
			if (!isPaused)
				return;
		}
		if (mediaPlayer.isPlaying()) {
			stop();
		}
		try {
			synchronized (this) {
				if (!isPaused) {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(context, song.getPathUri());
					if (!isPrepared) {
						prepare();
					}
				}
				mediaPlayer.start();
				currentPlaying = song;
			}
		} catch (Exception ex) {
			this.prepare();
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

	public Song getCurrentPlaying() {
		return currentPlaying;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public OnCompletionListener getOnCompletionListener() {
		return onCompletionListener;
	}

	public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
		this.onCompletionListener = onCompletionListener;
	}
}