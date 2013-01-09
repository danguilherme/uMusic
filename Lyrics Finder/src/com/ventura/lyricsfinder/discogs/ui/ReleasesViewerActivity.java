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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.entity.Artist;
import com.ventura.lyricsfinder.discogs.entity.Release;
import com.ventura.lyricsfinder.discogs.entity.Track;
import com.ventura.lyricsfinder.exception.LazyInternetConnectionException;
import com.ventura.lyricsfinder.exception.NoInternetConnectionException;
import com.ventura.lyricsfinder.ui.BaseActivity;
import com.ventura.lyricsfinder.util.ImageLoader;

public class ReleasesViewerActivity extends BaseActivity {

	private List<Release> mCurrentReleases;

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

	private void buildReleasesList(List<Release> releases) {
		this.mCurrentReleases = releases;

		Collections.sort(mCurrentReleases);

		for (int i = mCurrentReleases.size() - 1; i > 0; i--) {
			final Release release = mCurrentReleases.get(i);
			final String releaseUrl = release.getUrl().toString();
			final LinearLayout releasePanel = (LinearLayout) this
					.getLayoutInflater().inflate(R.layout.release, null);

			ImageView thumb = (ImageView) releasePanel
					.findViewById(R.id.release_thumb);
			if (release.getThumb() != null) {
				thumb = (ImageView) releasePanel
						.findViewById(R.id.release_thumb);
				new ImageLoader(this).displayImage(release.getThumb().getUrl()
						.toString(), thumb);

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
			TextView type = (TextView) releasePanel
					.findViewById(R.id.release_type);
			TextView format = (TextView) releasePanel
					.findViewById(R.id.release_status);
			Button openTracksButton = (Button) releasePanel
					.findViewById(R.id.btn_release_open_tracks);

			openTracksButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					LinearLayout tracksContainer = (LinearLayout) releasePanel
							.findViewById(R.id.release_track_container);

					if (release.getTracksList() != null
							&& release.getTracksList().size() > 0) {
						if (tracksContainer.getVisibility() == View.VISIBLE) {
							((Button) view)
									.setCompoundDrawablesWithIntrinsicBounds(
											R.drawable.arrow_right, 0, 0, 0);
							tracksContainer.setVisibility(View.GONE);
							return;
						}

					} else {
						new GetReleaseTracksTask(getBaseContext(), release,
								releasePanel).execute();
					}

					((Button) view).setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.arrow_down, 0, 0, 0);

					tracksContainer.setVisibility(View.VISIBLE);
				}
			});

			title.setText(release.getTitle());
			year.setText(release.getYear() + "");
			type.setText(release.getType().toString());

			if (release.getFormat().equals("")) {
				format.setVisibility(View.GONE);
			} else {
				format.setText(release.getFormat());
			}

			((LinearLayout) this.findViewById(R.id.container))
					.addView(releasePanel);
		}
	}

	private void buildReleaseTracksList(List<Track> result,
			LinearLayout parentReleaseView) {
		LinearLayout tracksContainer = (LinearLayout) parentReleaseView
				.findViewById(R.id.release_track_container);

		for (int j = 0; j < result.size(); j++) {
			Track track = result.get(j);
			RelativeLayout trackPanel = (RelativeLayout) getLayoutInflater()
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

	private class GetReleasesTask extends AsyncTask<Void, Void, List<Release>> {

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
		protected List<Release> doInBackground(Void... params) {

			DiscogsService ds = new DiscogsService(getBaseContext(),
					getConsumer(sharedPreferences));
			List<Release> releases = null;
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
		protected void onPostExecute(List<Release> result) {
			super.onPostExecute(result);
			buildReleasesList(result);
			mProgressDialog.dismiss();
		}

	}

	private class GetReleaseTracksTask extends
			AsyncTask<Void, Void, List<Track>> {

		private Context mContext;
		private Release mRelease;
		private LinearLayout mParentReleaseView;

		public GetReleaseTracksTask(Context context, Release release,
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
		protected List<Track> doInBackground(Void... params) {

			DiscogsService ds = new DiscogsService(getBaseContext(),
					getConsumer(sharedPreferences));
			List<Track> tracksList = null;
			try {
				tracksList = ds.getReleaseTracks(this.mRelease);
			} catch (NoInternetConnectionException e) {
				e.printStackTrace();
			} catch (LazyInternetConnectionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(this.mContext, "An error ocurred, try again...",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			this.mRelease.setTracksList(tracksList);
			return tracksList;
		}

		@Override
		protected void onPostExecute(List<Track> result) {
			super.onPostExecute(result);
			buildReleaseTracksList(result, this.mParentReleaseView);
		}

	}

	// ****** EVENT HANDLERS *******

	public void onOpenTracksButtonClicked(View button) {
	}
}
