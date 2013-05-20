package com.ventura.musicexplorer.ui.release;

import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.discogs.DiscogsService;
import com.ventura.musicexplorer.discogs.entity.Artist;
import com.ventura.musicexplorer.discogs.entity.ArtistRelease;
import com.ventura.musicexplorer.discogs.entity.Release;
import com.ventura.musicexplorer.discogs.entity.Track;
import com.ventura.musicexplorer.ui.BaseActivity;
import com.ventura.musicexplorer.ui.artist.ArtistViewerActivity_;
import com.ventura.musicexplorer.ui.widget.ButtonGroup;
import com.ventura.musicexplorer.ui.widget.KeyValuePanel;
import com.ventura.musicexplorer.util.ImageLoader;

@EActivity(R.layout.release_info)
public class ReleaseViewerActivity extends BaseActivity {
	private final String TAG = this.getClass().getName();

	@ViewById(R.id.content)
	LinearLayout contentContainer;

	@ViewById(R.id.tracks_list_container)
	LinearLayout tracksContainer;

	@ViewById(R.id.loadingReleaseInfoProgressBar)
	ProgressBar loadingProgressBar;

	@ViewById(R.id.release_image)
	ImageView thumb;

	@ViewById(R.id.artists_button_group)
	ButtonGroup artists;

	@ViewById(R.id.extra_artists_button_group)
	ButtonGroup extraArtists;

	@ViewById(R.id.notes)
	TextView notes;

	@ViewById(R.id.country)
	KeyValuePanel country;

	@ViewById(R.id.year)
	KeyValuePanel year;

	ArtistRelease artistRelease;
	Release release;

	@AnimationRes // Injects android.R.anim.fade_in
	Animation fadeIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		artistRelease = (ArtistRelease) intent
				.getSerializableExtra(ArtistRelease.KEY);

		updateActivityTitle();
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
		updateActivityTitle();

		if (loadingProgressBar != null
				&& loadingProgressBar.getVisibility() == View.VISIBLE) {
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}

		String notesText = release.getNotes();
		if (notesText == null) {
			notesText = "None";
		}
		notes.setText(notesText);
		country.setValue(release.getCountry());
		year.setValue(String.valueOf(release.getYear()));

		if (release.getThumbImage() != null) {
			ImageLoader imageLoader = new ImageLoader(this);
			imageLoader.displayImage(release.getThumbImage().getUrl()
					.toString(), thumb);
		}

		buildTracksView();
		buildArtistsView();
		buildExtraArtistsView();

		// Make content visible.
		contentContainer.startAnimation(fadeIn);
		contentContainer.setVisibility(View.VISIBLE);
	}

	private void updateActivityTitle() {
		getSupportActionBar().setTitle(artistRelease.getTitle());
		getSupportActionBar().setSubtitle(
				R.string.title_activity_release_viewer);
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

	private void buildArtistsView() {
		if (this.release.getArtists().size() > 0) {
			Collections.sort(this.release.getArtists());

			for (final Artist artist : this.release.getArtists()) {
				Button button = (Button) this.getLayoutInflater().inflate(
						R.layout.button_group_item, null);

				button.setText(artist.getName());

				if (artist.isActive()) {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.green_mark, 0, 0, 0);
				} else {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.red_mark, 0, 0, 0);
				}

				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						openArtistInfo(artist);
					}
				});
				artists.addButton(button);
			}
			
			artists.setVisibility(View.VISIBLE);
		}
	}

	private void buildExtraArtistsView() {
		if (this.release.getExtraArtists().size() > 0) {
			Collections.sort(this.release.getExtraArtists());

			for (final Artist artist : this.release.getExtraArtists()) {
				Button button = (Button) this.getLayoutInflater().inflate(
						R.layout.button_group_item, null);

				button.setText(artist.getName());
				Log.i(TAG, artist.getName());

				if (artist.isActive()) {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.green_mark, 0, 0, 0);
				} else {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.red_mark, 0, 0, 0);
				}

				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						openArtistInfo(artist);
					}
				});
				extraArtists.addButton(button);
			}
			
			extraArtists.setVisibility(View.VISIBLE);
		}
	}

	@UiThread
	protected void openArtistInfo(Artist artist) {
		Intent artistInfoIntent = new Intent(this, ArtistViewerActivity_.class);
		
		artistInfoIntent.putExtra(Artist.KEY_ID, artist.getId());
		artistInfoIntent.putExtra(Artist.KEY_NAME, artist.getName());
		
		startActivity(artistInfoIntent);
	}
}
