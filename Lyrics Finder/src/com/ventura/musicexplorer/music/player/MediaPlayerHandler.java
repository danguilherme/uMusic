package com.ventura.musicexplorer.music.player;

import android.os.Handler;
import android.os.Message;

public class MediaPlayerHandler extends Handler {
	private final String TAG = getClass().getName();
	
	protected MediaPlayerHandler() {
	}
	
	float mCurrentVolume = 1.0f;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MultiPlayer.MESSAGE_TRACK_WENT_TO_NEXT:
			// mPlayPos = mNextPlayPos;
			// if (mCursor != null) {
			// mCursor.close();
			// mCursor = null;
			// }
			// mCursor = getCursorForId(mPlayList[mPlayPos]);
			// notifyChange(META_CHANGED);
			// updateNotification();
			// setNextTrack();
			break;
		case MultiPlayer.MESSAGE_TRACK_ENDED:
			// if (mRepeatMode == REPEAT_CURRENT) {
			// seek(0);
			// play();
			// } else {
			// gotoNext(false);
			// }
			break;
		case MultiPlayer.MESSAGE_RELEASE_WAKELOCK:
			// mWakeLock.release();
			break;
		default:
			break;
		}
	}
}
