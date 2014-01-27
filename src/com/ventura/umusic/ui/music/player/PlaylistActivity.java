package com.ventura.umusic.ui.music.player;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.umusic.R;
import com.ventura.umusic.entity.music.Audio;
import com.ventura.umusic.music.player.MusicPlayerService;
import com.ventura.umusic.ui.BaseListActivity;

@EActivity(R.layout.default_list)
public class PlaylistActivity extends BaseListActivity implements
		OnItemClickListener {
	private final String TAG = getClass().getName();

	public static final String ACTION_CHOOSE_SONG = "com.ventura.umusic.ui.music.player.PlaylistActivity.ACTION_CHOOSE_SONG";

	/**
	 * Request a music to be returned by this activity
	 */
	public static final int REQUEST_SONG_CHOOSE = 1;

	public static final String EXTRA_PLAYLIST_ARRAY = "EXTRA_PLAYLIST_ARRAY";
	public static final String EXTRA_PLAYING = "EXTRA_PLAYING";

	@ViewById(android.R.id.list)
	protected ListView list;

	@ViewById(R.id.loadingListProgressBar)
	protected ProgressBar pgbProgressIndicator;

	private PlaylistAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter musicPlayerFilter = new IntentFilter();
		musicPlayerFilter.addAction(MusicPlayerService.ACTION_MUSIC_CHANGED);
		registerReceiver(mMusicPlayerActionsReceiver, musicPlayerFilter);
	}

	@AfterViews
	void afterViews() {
		decipherIntent();
		list.setOnItemClickListener(this);
	}

	private void decipherIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();

		if (PlaylistActivity.ACTION_CHOOSE_SONG.equals(action)) {
			loadPlaylistFromExtra();
			return;
		}

		// if it was an incorrect action
		finish();
	}

	BroadcastReceiver mMusicPlayerActionsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			adapter.setNowPlaying(intent.getStringExtra(Audio.KEY_URI));
		}
	};

	@SuppressWarnings("unchecked")
	private void loadPlaylistFromExtra() {
		Intent intent = getIntent();
		List<String> playlist = (List<String>) intent
				.getSerializableExtra(EXTRA_PLAYLIST_ARRAY);
		final String nowPlaying = intent.getStringExtra(EXTRA_PLAYING);
		adapter = new PlaylistAdapter(this, playlist);
		list.setAdapter(adapter);
		list.setSelection(adapter.getItemPosition(nowPlaying));
		adapter.setNowPlaying(nowPlaying);
	}

	// LISTENERS

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Intent intent = new Intent(this, MusicPlayerService.class);
		intent.putExtra(Audio.KEY_URI, adapter.get(position));
		intent.setAction(MusicPlayerService.ACTION_PLAY);
		startService(intent);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMusicPlayerActionsReceiver);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
	}
}
