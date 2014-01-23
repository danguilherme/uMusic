package com.ventura.umusic.ui.music.player;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
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
import com.ventura.umusic.ui.BaseListActivity;

@EActivity(R.layout.default_list)
public class PlaylistActivity extends BaseListActivity implements
		OnItemClickListener {

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

	@SuppressWarnings("unchecked")
	private void loadPlaylistFromExtra() {
		Intent intent = getIntent();
		List<String> playlist = (List<String>) intent
				.getSerializableExtra(EXTRA_PLAYLIST_ARRAY);
		final String nowPlaying = intent.getStringExtra(EXTRA_PLAYING);
		adapter = new PlaylistAdapter(this, playlist);
		list.setAdapter(adapter);
		list.setSelection(adapter.getItemPosition(nowPlaying));
		// getListView().post(new Runnable() {
		// @Override
		// public void run() {
		// list.smoothScrollToPositionFromTop(
		// adapter.getItemPosition(nowPlaying), 0);
		// }
		// });
	}

	// LISTENERS

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(Audio.KEY_URI, adapter.get(position));
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	@Override
	public void startActivity(Intent intent) {

		overridePendingTransition(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right);
		super.startActivity(intent);
	}

	@Override
	public void finish() {
		overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
		super.finish();
	}
}
