package com.ventura.umusic.music.player;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.ventura.umusic.entity.music.Audio;

public interface MusicPlayerListener extends OnCompletionListener {

	void onMusicChanged(Audio oldSong, Audio newSong);
	
	@Override
	public void onCompletion(MediaPlayer mp);
}
