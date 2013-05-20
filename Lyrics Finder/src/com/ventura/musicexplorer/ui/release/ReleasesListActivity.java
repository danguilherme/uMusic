package com.ventura.musicexplorer.ui.release;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.discogs.DiscogsService;
import com.ventura.musicexplorer.discogs.entity.ArtistRelease;
import com.ventura.musicexplorer.entity.artist.Artist;
import com.ventura.musicexplorer.entity.enumerator.ReleaseType;
import com.ventura.musicexplorer.ui.BaseListActivity;

@EActivity(R.layout.default_list)
public class ReleasesListActivity extends BaseListActivity {

	@ViewById(android.R.id.list)
	protected ListView list;

	@ViewById(R.id.loadingListProgressBar)
	protected ProgressBar loadingProgressBar;

	private ReleasesListAdapter listAdapter;
	private Artist mArtist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();

		this.mArtist = (Artist) intent.getSerializableExtra(Artist.KEY);
	}

	@AfterViews
	protected void afterViews() {
		this.getArtistReleases(this.mArtist.getId());
	}

	@UiThread
	protected void updateListView(List<ArtistRelease> data) {
		if (loadingProgressBar.getVisibility() == View.VISIBLE) {
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}

		if (this.listAdapter == null) {
			listAdapter = new ReleasesListAdapter(this, data);
		} else {
			this.listAdapter.addItems(data);
		}
		list.setAdapter(listAdapter);
	}

	@Background
	protected void getArtistReleases(int artistId) {
		DiscogsService discogsService = new DiscogsService(this, null);
		try {
			updateListView(discogsService.getArtistReleases(artistId));
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
		}
		updateListView(new ArrayList<ArtistRelease>());
	}

	@ItemClick(android.R.id.list)
	public void onArtistReleaseClicked(ArtistRelease artistRelease) {
		Intent openDetailedInfoIntent;
		if (artistRelease.getType().equals(
				ReleaseType.Master.toString().toLowerCase(Locale.US))) {
			openDetailedInfoIntent = new Intent(this,
					MasterViewerActivity_.class);
		} else {
			openDetailedInfoIntent = new Intent(this,
					ReleaseViewerActivity_.class);
		}
		openDetailedInfoIntent.putExtra(ArtistRelease.KEY, artistRelease);
		
		startActivity(openDetailedInfoIntent);
	}
}
