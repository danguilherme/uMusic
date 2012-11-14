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
		String lyricId = intent.getStringExtra(GlobalConstants.EXTRA_LYRIC_ID);
		
		TextView lyricsTextView = (TextView) this.findViewById(R.id.lyrics_text_view);
		TextView artistNameTextView = (TextView) this.findViewById(R.id.artist_text_view);
		TextView musicNameTextView = (TextView) this.findViewById(R.id.music_text_view);
		
		LyrDBService lyricsService = new LyrDBService(this);

		lyricsTextView.setText(lyricsService.getLyric(lyricId));
		artistNameTextView.setText(intent.getStringExtra(GlobalConstants.EXTRA_ARTIST_NAME));
		musicNameTextView.setText(intent.getStringExtra(GlobalConstants.EXTRA_TRACK_NAME));
		
		
	}
}
