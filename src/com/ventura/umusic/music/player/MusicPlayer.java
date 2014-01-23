package com.ventura.umusic.music.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.util.Log;

import com.ventura.umusic.entity.music.Audio;
import com.ventura.umusic.music.TracksManager;

/**
 * Facilitates the work with music playlists
 * 
 * @author Guilherme
 * 
 */
public class MusicPlayer implements OnCompletionListener {
	final String TAG = getClass().getName();

	private MediaPlayer mediaPlayer;
	private boolean isPaused = false;

	private MusicPlayerListener musicPlayerListener;

	private Context context;
	/**
	 * List of the uris of the songs that will play.
	 */
	private List<String> playlist;
	/**
	 * The shuffled copy of {@link playlist}.
	 */
	private List<String> shuffledPlaylist;
	/**
	 * The index inside {@link playlist} of the music that is playing at the
	 * moment.
	 */
	private int nowPlayingIdx = -1;

	private boolean isShuffle;
	private boolean isRepeat;

	public MusicPlayer(Context context) {
		this.context = context;
		this.initializeMediaPlayer();
	}

	public MusicPlayer(Context context, List<String> playlist) {
		this.context = context;
		this.initializeMediaPlayer();
		this.playlist = playlist;
	}

	private void initializeMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.reset();
		mediaPlayer.setOnCompletionListener(this);
	}

	public void onCompletion(MediaPlayer mediaPlayer) {
		synchronized (this) {
			mediaPlayer.reset();
		}
		if (musicPlayerListener != null)
			musicPlayerListener.onCompletion(mediaPlayer);

		next();
	}

	private void prepare() {
		try {
			mediaPlayer.prepare();
		} catch (Exception e) {
			Log.e(TAG, "Error when preparing Media Player", e);
		}
	}

	/**
	 * Plays a song. If this song is already playing, stop it and plays from the
	 * start.
	 * 
	 * @param song
	 *            The song path to play.
	 */
	private void play(Uri song) {
		if (song == null || song.equals(Uri.EMPTY)) {
			stop();
			return;
		}

		String nowPlayingMusic = getNowPlaying();

		if (getSongIdx(song.toString()) == -1) {// if not in playlist
			// create a new playlist with the song
			playlist = new ArrayList<String>();
			playlist.add(song.toString());
			setShuffle(false);
		}

		try {
			synchronized (this) {
				if (nowPlayingIdx > -1
						&& (nowPlayingIdx == getSongIdx(song.toString()) && song
								.toString().equals(nowPlayingMusic))) {
					if (isPaused)
						mediaPlayer.start();
					else {
						this.stop();
						this.play(song);
					}
				} else {
					stop();
					mediaPlayer.reset();
					mediaPlayer.setDataSource(context, song);
					prepare();
					mediaPlayer.start();
					nowPlayingIdx = getSongIdx(song.toString());
					
					onMusicChanged(nowPlayingMusic, song.toString());
				}
				mediaPlayer.setLooping(isRepeat);
				isPaused = false;
			}
		} catch (Exception ex) {
			this.initializeMediaPlayer();
			throw new RuntimeException("Couldn't load music", ex);
		}
	}

	/**
	 * Plays a song. If this song is already playing, stop it and plays from the
	 * start.
	 * 
	 * @param song
	 *            The song path to play.
	 */
	public void play(String song) {
		if (song == null)
			play(Uri.EMPTY);
		else
			play(Uri.parse(song));
	}

	/**
	 * Plays a song. If this song is already playing, stop it and plays from the
	 * start.
	 * 
	 * @param song
	 *            The song to play.
	 */
	public void play(Audio song) {
		play(song.getPathUri());
	}

	/**
	 * Resumes playback, if it was paused.
	 */
	public void play() {
		String nowPlaing = getNowPlaying();
		if (nowPlaing != null)
			play(nowPlaing);
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public void stop() {
		mediaPlayer.pause();
		mediaPlayer.seekTo(0);
		synchronized (this) {
			isPaused = true;
		}
	}

	public void next() {
		boolean mustBePaused = isPaused;
		String currentPlaying = getNowPlaying();
		if (nowPlayingIdx < (getPlaylist().size() - 1)) {
			this.play(getPlaylist().get(nowPlayingIdx + 1));
		} else {
			this.play(getPlaylist().get(0));
		}
		String musicChanged = getNowPlaying();

		onMusicChanged(currentPlaying, musicChanged);
		
		if (mustBePaused)
			pause();
	}

	public void prev() {
		boolean mustBePaused = isPaused;
		String currentPlaying = getNowPlaying();
		if (nowPlayingIdx > 0) {
			this.play(getPlaylist().get(nowPlayingIdx - 1));
		} else {
			this.play(getPlaylist().get(getPlaylist().size() - 1));
		}
		String musicChanged = getNowPlaying();

		onMusicChanged(currentPlaying, musicChanged);
		
		if (mustBePaused)
			pause();
	}

	public void pause() {
		mediaPlayer.pause();
		this.isPaused = true;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public boolean isLooping() {
		return mediaPlayer.isLooping();
	}

	public void setShuffle(boolean isShuffle) {
		String currentlyPlaying = getNowPlaying();
		this.isShuffle = isShuffle;
		if (isShuffle && this.playlist != null) {
			shuffledPlaylist = new ArrayList<String>(playlist);
			Collections.shuffle(shuffledPlaylist);
		}
		// update the index of the current song
		nowPlayingIdx = getSongIdx(currentlyPlaying);
	}

	public boolean isShuffle() {
		return isShuffle;
	}

	public void toggleShuffle() {
		this.setShuffle(!isShuffle);
	}

	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
		mediaPlayer.setLooping(isRepeat);
	}

	public boolean isRepeat() {
		return isRepeat;
	}

	public void toggleRepeat() {
		this.setRepeat(!isRepeat);
	}

	public void dispose() {
		if (isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
	}

	public Audio getCurrentPlaying() {
		return getAudioInfo(getNowPlaying());
	}

	private Audio getAudioInfo(String audio) {
		return new TracksManager(context).getTrackByUri(audio);
	}

	/**
	 * Gets the index of a song inside the actual playlist.
	 * 
	 * @param songPath
	 *            The path to search by
	 * @return The index of the song inside the {@link playlist}.
	 */
	private int getSongIdx(String songPath) {
		if (getPlaylist() != null)
			return getPlaylist().indexOf(songPath);
		return -1;
	}

	public void setPlaylist(List<String> playlist) {
		stop();
		this.playlist = playlist;
		this.setShuffle(isShuffle); // reshuffle, if applied
		if (playlist != null) {
			this.play(getPlaylist().get(0));
		}
	}

	/**
	 * Returns the playlist that is currently in use by the player.
	 * 
	 * @return The playlist that is being played
	 */
	public List<String> getPlaylist() {
		if (isShuffle)
			return shuffledPlaylist;
		else
			return playlist;
	}

	private String getNowPlaying() {
		if (getPlaylist() != null && nowPlayingIdx > -1)
			return getPlaylist().get(nowPlayingIdx);
		return null;
	}

	public void setListener(MusicPlayerListener listener) {
		this.musicPlayerListener = listener;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void seekTo(int msec) {
		mediaPlayer.seekTo(msec);
	}

	private void onMusicChanged(String currentPlaying, String musicChanged) {
		if (musicPlayerListener != null)
			musicPlayerListener.onMusicChanged(getAudioInfo(currentPlaying),
					getAudioInfo(musicChanged));
	}
}