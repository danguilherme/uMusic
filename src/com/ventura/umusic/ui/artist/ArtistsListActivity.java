package com.ventura.umusic.ui.artist;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.androidutils.utils.ConnectionManager;
import com.ventura.umusic.R;
import com.ventura.umusic.business.ArtistService;
import com.ventura.umusic.constants.GlobalConstants;
import com.ventura.umusic.entity.artist.Artist;
import com.ventura.umusic.entity.pagination.PaginatedList;
import com.ventura.umusic.ui.InfiniteListActivity;

@EActivity(R.layout.default_list)
public class ArtistsListActivity extends InfiniteListActivity implements
		OnItemClickListener {
	final String TAG = getClass().getName();

	private SharedPreferences prefs;

	@ViewById(android.R.id.list)
	protected ListView list;

	@ViewById(R.id.loadingListProgressBar)
	protected ProgressBar loadingProgressBar;

	private ArtistsListAdapter adapter;

	private PaginatedList<Artist> artistsFound = null;
	private LinearLayout loadMoreProgress;
	boolean isLoading = false;

	private ArtistService artistService = new ArtistService(this);
	/**
	 * Artist being searched
	 */
	private String query = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@AfterViews
	protected void afterViews() {
		list.setOnScrollListener(this);

		loadMoreProgress = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.load_more_progress, null);
		list.addFooterView(loadMoreProgress);

		Intent intent = this.getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i(TAG, "Search: " + query);
			return;
		}

		String queryText = intent
				.getStringExtra(GlobalConstants.EXTRA_QUERY);

		if (queryText == null)
			finish();

		this.listArtists(queryText);

		// Click event for single list row
		list.setOnItemClickListener(this);
	}

	@Override
	public boolean onSearchRequested() {
		Log.i(TAG, "Search requested");
		return super.onSearchRequested();
	}

	/**
	 * Pushes data into list adapter.
	 * 
	 * @param data
	 *            the data to push
	 */
	@UiThread
	protected void updateListView(PaginatedList<Artist> data) {
		if (loadingProgressBar.getVisibility() == View.VISIBLE)
			loadingProgressBar.setVisibility(View.INVISIBLE);

		if (data.getCount() <= 0) {
			Toast.makeText(this, "No singer was found", Toast.LENGTH_LONG)
					.show();
			this.finish();
			return;
		}

		if (artistsFound == null)
			artistsFound = new PaginatedList<Artist>(data);
		else
			artistsFound.addItems(data.getItems());

		list = (ListView) findViewById(android.R.id.list);

		// If the adapter doesn't exist, we create one with the initial data.
		// If it exists, we update it.
		int currentPosition = -1;
		if (adapter == null) {
			adapter = new ArtistsListAdapter(this, data.getItems());
		} else {
			currentPosition = list.getFirstVisiblePosition();
			adapter.addItems(data.getItems());
		}
		list.setAdapter(adapter);

		list.setSelectionFromTop(currentPosition + 1, 0);

		if (artistsFound.getPaging().isLast())
			list.removeFooterView(loadMoreProgress);
		
		this.isLoading = false;
	}

	@Background
	protected void listArtists(String query) {
		this.isLoading = true;
		PaginatedList<Artist> searchResult = PaginatedList.empty();

		this.query = query;

		try {
			searchResult = artistService.search(query);
		} catch (NoInternetConnectionException e) {
			showNoInternetMessage();
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			showLazyInternetMessage();
			e.printStackTrace();
		} catch (Exception e) {
			this.alert(e.getMessage());
			e.printStackTrace();
		}
		updateListView(searchResult);
	}

	@Background
	protected void loadMore() {
		this.isLoading = true;
		PaginatedList<Artist> searchResult = PaginatedList.empty();
		
		try {
			if (!artistsFound.getPaging().isLast()) {
				searchResult = artistService.search(query, 50, artistsFound
						.getPaging().getActualPage() + 1);
				artistsFound.getPaging().setActualPage(
						artistsFound.getPaging().getActualPage() + 1);
			}
		} catch (NoInternetConnectionException e) {
			showNoInternetMessage();
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			showLazyInternetMessage();
			e.printStackTrace();
		} catch (Exception e) {
			this.alert(e.getMessage());
			e.printStackTrace();
		}
		updateListView(searchResult);
	}

	@Override
	protected void onListEndAchieved() {
		if (artistsFound == null)
			return;

		if (!artistsFound.getPaging().isLast() && !this.isLoading)
			this.loadMore();
	}

	// OnItemClickListener
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view.getId() == android.R.id.progress) {
			// If the user clicked at the load more progress indicator,
			// do nothing.
			return;
		}

		// Verifying internet connection...
		if (!ConnectionManager.isConnected(this)) {
			Toast.makeText(this,
					this.getString(R.string.message_no_internet_connection),
					Toast.LENGTH_LONG).show();
		}

		Intent intent = new Intent(this, ArtistViewerActivity_.class);

		Artist artist = (Artist) adapter.getItem(position);
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Artist.KEY, artist);
		startActivity(intent);
	}
}
