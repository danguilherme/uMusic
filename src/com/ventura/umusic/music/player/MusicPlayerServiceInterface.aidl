// MusicPlayerServiceInterface.aidl
package com.ventura.umusic.music.player;

// Declare any non-default types here with import statements
import java.util.List;

/** Example service interface */
interface MusicPlayerServiceInterface {
    /** Request the process ID of this service, to do evil things with it. */
    int getPid();
	
	void clearPlaylist();
	
	void setPlaylist(in List<String> newPlaylist);
	
	List<String> getPlaylist();
	
	void play(in String songPath);
	
	void pause();
	
	void stop();
	
	void next();
	
	void prev();
	
	void seekTo(int position);
	
	boolean isPaused();
	
	boolean isShuffle();
	
	void setShuffle(boolean shuffle);
	
	boolean toggleShuffle();
	
	boolean isRepeat();
	
	void setRepeat(boolean repeat);
	
	boolean toggleRepeat();
	
	String getCurrentPlaying();
	
	int getDuration();
	
	int getCurrentDuration();
}