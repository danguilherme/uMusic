package com.ventura.umusic.music.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

public class MusicPlayerBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = getClass().getName();

	long prevEventTime = 0;
	@Override
	public void onReceive(Context context, Intent intent) {
		intent.setClass(context, MusicPlayerService.class);
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
			// TODO What can I do?
		}
		context.startService(intent);
	}
}