package com.ventura.lyricsfinder.lyrdb.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.ventura.lyricsfinder.GlobalConstants;
import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.lyrdb.LyrDBService;

public class LyricsViewerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lyric_view);

		Intent intent = this.getIntent();
		String artist = intent
				.getStringExtra(GlobalConstants.EXTRA_ARTIST_NAME);
		String music = intent.getStringExtra(GlobalConstants.EXTRA_TRACK_NAME);
		TextView lyricText = (TextView) this.findViewById(R.id.lyric_text_view);

		LyrDBService lyricsService = new LyrDBService(this);

		lyricText.setText(lyricsService.getLyric(lyricsService
				.search("fullt", artist, music).get(0).getId()));
	}
}
