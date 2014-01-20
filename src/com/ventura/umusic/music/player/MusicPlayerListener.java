package com.ventura.umusic.music.player;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.ventura.umusic.entity.music.Track;

public interface MusicPlayerListener extends OnCompletionListener {

	void onMusicChanged(Track oldSong, Track newSong);
	
	@Override
	public void onCompletion(MediaPlayer mp);
}
