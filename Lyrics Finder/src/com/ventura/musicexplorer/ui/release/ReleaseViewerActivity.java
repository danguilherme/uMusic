package com.ventura.musicexplorer.ui.release;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.discogs.DiscogsService;
import com.ventura.musicexplorer.discogs.entity.ArtistRelease;
import com.ventura.musicexplorer.discogs.entity.Release;
import com.ventura.musicexplorer.discogs.entity.Track;
import com.ventura.musicexplorer.ui.BaseActivity;

@EActivity(R.layout.release_info)
public class ReleaseViewerActivity extends BaseActivity {

	@ViewById(R.id.tracks_list_container)
	LinearLayout tracksContainer;
	
	@ViewById(R.id.loadingReleaseInfoProgressBar)
	ProgressBar loadingProgressBar;

	ArtistRelease artistRelease;
	Release release;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		artistRelease = (ArtistRelease) intent
				.getSerializableExtra(ArtistRelease.KEY);
		
		getSupportActionBar().setTitle(artistRelease.getTitle());
		getSupportActionBar().setSubtitle(R.string.title_activity_releases_viewer);
	}

	@AfterViews
	protected void afterViews() {
		this.getRelease(artistRelease);
	}

	@Background
	protected void getRelease(ArtistRelease artistRelease) {
		DiscogsService ds = new DiscogsService(this, null);
		try {
			release = ds.getRelease(artistRelease);
		} catch (Exception e) {

		}
		updateView(release);
	}

	@UiThread
	protected void updateView(Release release) {
		if (loadingProgressBar != null && loadingProgressBar.getVisibility() == View.VISIBLE) {
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
		
		buildTracksView();
	}

	private void buildTracksView() {
		if (release.getTracks() != null && release.getTracks().size() > 0) {
			for (Track track : release.getTracks()) {
				if (track.getPosition().equals("")
						&& track.getTitle().equals("")
						&& track.getDuration().equals("")) {
					continue;
				}

				if (track.getPosition().equals("0")) {
					track.setPosition("");
				}

				LinearLayout trackPanel = (LinearLayout) getLayoutInflater()
						.inflate(R.layout.track, null);

				TextView position = (TextView) trackPanel
						.findViewById(R.id.track_position);
				TextView title = (TextView) trackPanel
						.findViewById(R.id.track_title);
				TextView duration = (TextView) trackPanel
						.findViewById(R.id.track_duration);

				position.setText(track.getPosition() + "");
				title.setText(track.getTitle());
				duration.setText(track.getDuration());

				tracksContainer.addView(trackPanel);
			}
		}
	}
}
