package com.ventura.lyricsfinder.discogs.ui;

import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.entity.Artist;
import com.ventura.lyricsfinder.discogs.entity.ArtistRelease;
import com.ventura.lyricsfinder.discogs.entity.BasicRelease;
import com.ventura.lyricsfinder.discogs.entity.Label;
import com.ventura.lyricsfinder.discogs.entity.Master;
import com.ventura.lyricsfinder.discogs.entity.Release;
import com.ventura.lyricsfinder.discogs.entity.Track;
import com.ventura.lyricsfinder.discogs.entity.enumerator.SpecialEnums;
import com.ventura.lyricsfinder.exception.LazyInternetConnectionException;
import com.ventura.lyricsfinder.exception.NoInternetConnectionException;
import com.ventura.lyricsfinder.ui.BaseActivity;
import com.ventura.lyricsfinder.util.ImageLoader;

public class ReleasesViewerActivity extends BaseActivity {

	private List<ArtistRelease> mCurrentReleases;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.releases_view);

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
			final String releaseUrl = release.getUrl().toString();
			final LinearLayout releasePanel = (LinearLayout) this
					.getLayoutInflater().inflate(R.layout.artist_release, null);

			ImageView thumb = (ImageView) releasePanel
					.findViewById(R.id.release_thumb);
			if (release.getThumbImage().getUrl() != null) {
				thumb = (ImageView) releasePanel
						.findViewById(R.id.release_thumb);
				new ImageLoader(this).displayImage(release.getThumbImage()
						.getUrl().toString(), thumb);

				thumb.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri
								.parse(releaseUrl));
						startActivity(i);
					}
				});
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
					LinearLayout tracksContainer = (LinearLayout) releasePanel
							.findViewById(R.id.artist_release_info_container);

					if (release.getChildRelease() != null
							&& release.getChildRelease().getTracks().size() > 0) {
						if (tracksContainer.getVisibility() == View.VISIBLE) {
							((Button) view)
									.setCompoundDrawablesWithIntrinsicBounds(
											R.drawable.arrow_right, 0, 0, 0);
							tracksContainer.setVisibility(View.GONE);
							return;
						}

					} else {
						new GetReleaseOrMasterTask(getBaseContext(), release,
								releasePanel).execute();
					}

					((Button) view).setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.arrow_down, 0, 0, 0);

					tracksContainer.setVisibility(View.VISIBLE);
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
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
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

	private void buildTrackListView(BasicRelease release,
			LinearLayout parentReleaseView) {
		LinearLayout tracksContainer = (LinearLayout) parentReleaseView
				.findViewById(R.id.artist_release_info_container);

		for (int j = 0; j < release.getTracks().size(); j++) {
			Track track = release.getTracks().get(j);
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
			AsyncTask<Void, Void, List<ArtistRelease>> {

		private ProgressDialog mProgressDialog;
		private Context mContext;
		private Artist mArtist;

		public GetReleasesTask(Context context, Artist artist) {

			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_releases_title));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_releases_body));
			this.mProgressDialog.setCancelable(true);
			this.mProgressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cancel(true);
					finish();
				}
			});
			this.mContext = context;
			this.mArtist = artist;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.mProgressDialog.show();
		}

		@Override
		protected List<ArtistRelease> doInBackground(Void... params) {

			DiscogsService ds = new DiscogsService(getBaseContext(),
					getConsumer(sharedPreferences));
			List<ArtistRelease> releases = null;
			try {
				releases = ds.getArtistReleases(this.mArtist.getId());
			} catch (NoInternetConnectionException e) {
				e.printStackTrace();
			} catch (LazyInternetConnectionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(this.mContext, "An error ocurred, try again...",
						Toast.LENGTH_LONG).show();
				finish();
				e.printStackTrace();
			}
			return releases;
		}

		@Override
		protected void onPostExecute(List<ArtistRelease> result) {
			super.onPostExecute(result);
			buildArtistReleasesList(result);
			mProgressDialog.dismiss();
		}

	}

	private class GetReleaseOrMasterTask extends
			AsyncTask<Void, Void, BasicRelease> {

		private Context mContext;
		private ArtistRelease mRelease;
		private LinearLayout mParentReleaseView;

		public GetReleaseOrMasterTask(Context context, ArtistRelease release,
				LinearLayout parentReleaseView) {
			this.mContext = context;
			this.mRelease = release;
			this.mParentReleaseView = parentReleaseView;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected BasicRelease doInBackground(Void... params) {

			DiscogsService ds = new DiscogsService(getBaseContext(),
					getConsumer(sharedPreferences));
			BasicRelease release = null;
			try {
				if (this.mRelease.getType().equals(
						SpecialEnums.ARTIST_RELEASE_TYPE_RELEASE)) {
					release = ds.getRelease(this.mRelease);
				} else {
					release = ds.getMaster(this.mRelease);
				}
			} catch (NoInternetConnectionException e) {
				e.printStackTrace();
			} catch (LazyInternetConnectionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(this.mContext, "An error ocurred, try again...",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			this.mRelease.setChildRelease(release);
			return release;
		}

		@Override
		protected void onPostExecute(BasicRelease result) {
			super.onPostExecute(result);
			if (result.getType().equals(
					SpecialEnums.ARTIST_RELEASE_TYPE_RELEASE)) {
				buildReleaseInfoView((Release) result, this.mParentReleaseView);
			} else if (result.getType().equals(
					SpecialEnums.ARTIST_RELEASE_TYPE_MASTER)) {
				buildMasterInfoView((Master) result, this.mParentReleaseView);
			}
		}

	}

	// ****** EVENT HANDLERS *******

	public void onOpenTracksButtonClicked(View button) {
	}
}
