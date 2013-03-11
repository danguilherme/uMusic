package com.ventura.lyricsfinder.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.lyricsfinder.business.ArtistService;
import com.ventura.lyricsfinder.entity.artist.Artist;
import com.ventura.musicexplorer.R;

public class DashboardActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.dashboard);
	}

	public void onSearchArtistButtonClick(View button) {
		this.startActivity(new Intent(this, MusicInfoActivity.class));
	}

	public void onSearchLyricsButtonClick(View button) {
		ArtistService as = new ArtistService(this);

		List<Artist> artists = new ArrayList<Artist>();
		try {
			artists = as.search("Evanescence");
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
		}
		alert(artists.toString());
		// this.startActivity(new Intent(this, MusicInfoActivity.class));
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}
}
