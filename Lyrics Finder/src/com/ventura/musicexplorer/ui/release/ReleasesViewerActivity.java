package com.ventura.musicexplorer.ui.release;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.androidutils.utils.ConnectionManager;
import com.ventura.androidutils.utils.InnerActivityAsyncTask;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.business.ArtistService;
import com.ventura.musicexplorer.business.ReleaseService;
import com.ventura.musicexplorer.constants.GlobalConstants;
import com.ventura.musicexplorer.entity.artist.Artist;
import com.ventura.musicexplorer.entity.enumerator.ReleaseType;
import com.ventura.musicexplorer.entity.release.ArtistRelease;
import com.ventura.musicexplorer.entity.release.Master;
import com.ventura.musicexplorer.entity.release.Release;
import com.ventura.musicexplorer.entity.release.Track;
import com.ventura.musicexplorer.ui.BaseActivity;
import com.ventura.musicexplorer.util.ImageLoader;

public class ReleasesViewerActivity extends BaseActivity {

	private List<ArtistRelease> mCurrentReleases;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.releases_view);

		// Enable navigation to parentActivity.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent initialIntent = this.getIntent();

		Bundle extras = initialIntent.getExtras();

		Artist artist = new Artist(
				extras.getInt(GlobalConstants.EXTRA_ARTIST_ID),
				extras.getString(GlobalConstants.EXTRA_ARTIST_NAME), null);

		new GetReleasesTask(this, artist).execute();
	}

	private void buildArtistReleasesList(List<ArtistRelease> releases) {
		this.mCurrentReleases = releases;

		Collections.sort(mCurrentReleases);

		for (int i = mCurrentReleases.size() - 1; i >= 0; i--) {
			final ArtistRelease release = mCurrentReleases.get(i);
			final LinearLayout releasePanel = (LinearLayout) this
					.getLayoutInflater().inflate(R.layout.artist_release, null);

			if (release.getThumbImage() != null
					&& release.getThumbImage().getUrl() != null) {
				ImageView thumb = (ImageView) releasePanel
						.findViewById(R.id.release_thumb);
				new ImageLoader(this).displayImage(release.getThumbImage()
						.getUrl().toString(), thumb);
			} else {
				LinearLayout thumbContainer = (LinearLayout) releasePanel
						.findViewById(R.id.thumbnail);
				thumbContainer.setVisibility(View.GONE);
			}

			TextView title = (TextView) releasePanel
					.findViewById(R.id.release_title);
			TextView year = (TextView) releasePanel
					.findViewById(R.id.release_year);
			TextView trackInfo = (TextView) releasePanel
					.findViewById(R.id.release_trackinfo);
			TextView format = (TextView) releasePanel
					.findViewById(R.id.release_status);
			Button openTracksButton = (Button) releasePanel
					.findViewById(R.id.btn_artist_release_open_info);

			openTracksButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					showReleaseInfo((Button) view, release, releasePanel);
				}
			});

			title.setText(release.getTitle());
			year.setText(release.getYear() + "");
			trackInfo.setText(release.getTrackInfo());
			format.setVisibility(View.GONE);
			/*
			 * if (release.getFormat().equals("")) {
			 * format.setVisibility(View.GONE); } else {
			 * format.setText(release.getFormat()); }
			 */

			((LinearLayout) this.findViewById(R.id.container))
					.addView(releasePanel);
		}
	}

	private void showReleaseInfo(Button showInfoButton, ArtistRelease release,
			LinearLayout releasePanel) {
		LinearLayout tracksContainer = (LinearLayout) releasePanel
				.findViewById(R.id.artist_release_info_container);

		// If the user is not connected, do nothing
		if (!ConnectionManager.isConnected(this)) {
			Toast.makeText(this,
					this.getString(R.string.message_no_internet_connection),
					Toast.LENGTH_LONG).show();
			return;
		}

		// Verify if this release's tracks weren't already
		// downloaded.
		if (release.isComplete() && release.getTrackList().size() > 0) {
			// if yes, just reopen the container.
			if (tracksContainer.getVisibility() == View.VISIBLE) {
				showInfoButton.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.arrow_right, 0, 0, 0);
				tracksContainer.setVisibility(View.GONE);
				return;
			}

		} else {
			// else, load the release's tracks
			new GetReleaseOrMasterTask(getBaseContext(), release, releasePanel)
					.execute();
		}

		showInfoButton.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.arrow_down, 0, 0, 0);

		tracksContainer.setVisibility(View.VISIBLE);
	}

	private void buildReleaseInfoView(Release result,
			LinearLayout parentReleaseView) {

		LinearLayout releaseInfoContainer = (LinearLayout) parentReleaseView
				.findViewById(R.id.artist_release_info_container);

		LinearLayout releasePanel = (LinearLayout) getLayoutInflater().inflate(
				R.layout.release, null);

		boolean hasContent = false;

		if (result.getLabels() != null && result.getLabels().size() != 0) {
			TextView txtLabelName = (TextView) releasePanel
					.findViewById(R.id.label);
			txtLabelName.setText(this.createList(result.getLabels()));
			releasePanel.findViewById(R.id.label_container).setVisibility(
					View.VISIBLE);
			hasContent = true;
		}

		if (result.getGenres() != null && result.getGenres().size() != 0) {
			TextView txtGenres = (TextView) releasePanel
					.findViewById(R.id.genres);
			txtGenres.setText(this.createList(result.getGenres()));
			releasePanel.findViewById(R.id.genres_container).setVisibility(
					View.VISIBLE);
			hasContent = true;
		}

		if (result.getStyles() != null && result.getStyles().size() != 0) {
			TextView txtStyles = (TextView) releasePanel
					.findViewById(R.id.styles);
			txtStyles.setText(this.createList(result.getStyles()));
			releasePanel.findViewById(R.id.styles_container).setVisibility(
					View.VISIBLE);
			hasContent = true;
		}

		if (result.getCountry() != null && !result.getCountry().equals("")) {
			TextView txtCountry = (TextView) releasePanel
					.findViewById(R.id.country);
			txtCountry.setText(result.getCountry());
			releasePanel.findViewById(R.id.country_container).setVisibility(
					View.VISIBLE);
			hasContent = true;
		}

		if (hasContent) {
			LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			linearLayoutParams.setMargins(0, 0, 0, 10);
			releasePanel.setLayoutParams(linearLayoutParams);

			releaseInfoContainer.addView(releasePanel);
		}

		this.buildTrackListView(result, parentReleaseView);
	}

	private void buildMasterInfoView(Master result,
			LinearLayout parentReleaseView) {

		this.buildTrackListView(result, parentReleaseView);
	}

	private void buildTrackListView(ArtistRelease release,
			LinearLayout parentReleaseView) {
		LinearLayout tracksContainer = (LinearLayout) parentReleaseView
				.findViewById(R.id.artist_release_info_container);

		for (int j = 0; j < release.getTrackList().size(); j++) {
			Track track = release.getTrackList().get(j);
			if (track.getPosition().equals("") && track.getTitle().equals("")
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

		((ProgressBar) tracksContainer.findViewById(android.R.id.progress))
				.setVisibility(View.GONE);
	}

	private class GetReleasesTask extends
			InnerActivityAsyncTask<Void, Void, List<ArtistRelease>> {
		private Artist mArtist;

		public GetReleasesTask(Context context, Artist artist) {
			super(context, getString(R.string.app_name),
					getString(R.string.message_fetching_releases_body));
			this.mArtist = artist;
		}

		@Override
		protected List<ArtistRelease> doInBackground(Void... params) {
			/*
			 * DiscogsService ds = new DiscogsService(getBaseContext(),
			 * getConsumer(sharedPreferences));
			 */
			ArtistService as = new ArtistService(this.getContext());
			List<ArtistRelease> releases = null;
			try {
				releases = as.getArtistReleases(this.mArtist.getId());
			} catch (NoInternetConnectionException e) {
				e.printStackTrace();
			} catch (LazyInternetConnectionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				/*
				 * Toast.makeText(this.getContext(),
				 * "An error ocurred, try again...", Toast.LENGTH_LONG) .show();
				 */
				finish();
				e.printStackTrace();
			}
			return releases;
		}

		@Override
		protected void onPostExecute(List<ArtistRelease> result) {
			super.onPostExecute(result);
			buildArtistReleasesList(result);
		}

		@Override
		public void onProgressDialogCancelled(DialogInterface progressDialog) {
			cancel(true);
			finish();
		}

	}

	private class GetReleaseOrMasterTask extends
			AsyncTask<Void, Void, ArtistRelease> {

		private Context mContext;
		private ArtistRelease mRelease;
		private LinearLayout mParentReleaseView;
		private String TAG = getClass().getName();

		public GetReleaseOrMasterTask(Context context, ArtistRelease release,
				LinearLayout parentReleaseView) {
			this.mContext = context;
			this.mRelease = release;
			this.mParentReleaseView = parentReleaseView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!ConnectionManager.isConnected(mContext)) {
				alert(mContext
						.getString(R.string.message_no_internet_connection));
				this.cancel(true);
			}
		}

		@Override
		protected ArtistRelease doInBackground(Void... params) {
			ReleaseService releaseService = new ReleaseService(mContext);
			ArtistRelease release = new ArtistRelease();
			try {
				switch (this.mRelease.getType()) {
				case Release:
					release = releaseService.getRelease(this.mRelease.getId());
					break;
				case Master:
					release = releaseService.getMaster(this.mRelease.getId());
					break;
				default:
					throw new Exception("Release type not supported");
				}
				release.isComplete(true);
			} catch (Exception e) {
				Log.e(TAG, "Error on getting detailed release info.", e);
			}
			return release;
		}

		@Override
		protected void onPostExecute(ArtistRelease result) {
			super.onPostExecute(result);
			try {
				if (result == null) {
					finish();
					result = new ArtistRelease();
					result.setType(ReleaseType.Master);
				}
				switch (result.getType()) {
				case Release:
					buildReleaseInfoView((Release) result,
							this.mParentReleaseView);
					break;
				case Master:
					buildMasterInfoView((Master) result,
							this.mParentReleaseView);
					break;
				default:
					break;
				}

			} catch (Exception e) {
				Toast.makeText(
						ReleasesViewerActivity.this,
						mContext.getString(R.string.error_bringing_release_info),
						Toast.LENGTH_LONG).show();
				Log.e(TAG, "Error when showing release/master info", e);
				// finish();
			}
		}

	}

	// ****** EVENT HANDLERS *******
	public void onOpenTracksButtonClicked(View button) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
