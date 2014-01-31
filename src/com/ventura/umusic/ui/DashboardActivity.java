package com.ventura.umusic.ui;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.ventura.umusic.R;
import com.ventura.umusic.ui.music.player.MusicPlayerActivity_;

public class DashboardActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.dashboard);
	}

	public void onMusicPlayerButtonClick(View button) {
		this.startActivity(new Intent(this, MusicPlayerActivity_.class));
	}

	public void onSearchArtistButtonClick(View button) {
		//this.startActivity(new Intent(this, MusicInfoActivity_.class));
		alert(R.string.message_coming_soon);
	}

	public void onSearchLyricsButtonClick(View button) {
		alert(R.string.message_coming_soon);
	}

	public void onAboutButtonClick(View button) {
		this.startActivity(new Intent(this, AboutActivity_.class));
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}
}
