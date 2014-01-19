package com.ventura.umusic.music;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.ventura.umusic.entity.music.Track;

/**
 * Facilitates the work with music playlists
 * 
 * @author Guilherme
 * 
 */
public class MusicPlayer implements OnCompletionListener {
	final String TAG = getClass().getName();

	private MediaPlayer mediaPlayer;
	private boolean isPrepared = false;
	private boolean isPaused = false;

	private OnCompletionListener onCompletionListener;

	/**
	 * The song that is playing at the moment
	 */
	private Track currentPlaying;
	private Context context;
	/**
	 * List of the uris of the songs that will play.
	 */
	private String[] playlist;
	/**
	 * The index inside {@link playlist} of the music that is played.
	 */
	private int nowPlaying;

	public MusicPlayer(Context context) {
		this.context = context;
		this.initializeMediaPlayer();
	}

	public MusicPlayer(Context context, String[] playlist) {
		this.context = context;
		this.initializeMediaPlayer();
		this.playlist = playlist;
	}

	private void initializeMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();
		mediaPlayer.setOnCompletionListener(this);
		currentPlaying = null;
	}

	public void onCompletion(MediaPlayer mediaPlayer) {
		synchronized (this) {
			mediaPlayer.reset();
			isPrepared = false;
		}
		if (onCompletionListener != null)
			onCompletionListener.onCompletion(mediaPlayer);

		next();
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
	 * Plays a song. If this song is already playing, stop it and plays from the
	 * start.
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
					else {
						this.stop();
						this.play(song);
					}
				} else {
					stop();
					mediaPlayer.reset();
					mediaPlayer.setDataSource(context, song.getPathUri());
					prepare();
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
		isPaused = true;
		synchronized (this) {
			isPrepared = false;
		}
	}

	private void next() {
		// TODO Auto-generated method stub

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
		if (isPlaying()) {
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