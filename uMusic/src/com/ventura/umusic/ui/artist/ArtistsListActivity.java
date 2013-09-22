package com.ventura.umusic.ui.artist;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.ventura.umusic.discogs.DiscogsConstants;
import com.ventura.umusic.discogs.DiscogsService;
import com.ventura.umusic.discogs.entity.SearchItem;
import com.ventura.umusic.discogs.entity.SearchResult;
import com.ventura.umusic.discogs.entity.enumerator.QueryType;
import com.ventura.umusic.entity.Image;
import com.ventura.umusic.entity.artist.Artist;
import com.ventura.umusic.ui.BaseListActivity;

@EActivity(R.layout.default_list)
public class ArtistsListActivity extends BaseListActivity implements
		OnScrollListener, OnItemClickListener {
	final String TAG = getClass().getName();

	private SharedPreferences prefs;
	
	@ViewById(android.R.id.list)
	protected ListView list;
	
	@ViewById(R.id.loadingListProgressBar)
	protected ProgressBar loadingProgressBar;

	private ArtistsListAdapter adapter;

	private OAuthConsumer discogsCustomer;
	private SearchResult currentSearchResult;
	private LinearLayout loadMoreProgress;
	boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		discogsCustomer = new ArtistViewerActivity().getConsumer(this.prefs);
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

		QueryType queryType = Enum.valueOf(QueryType.class,
				intent.getStringExtra(DiscogsConstants.KEY_QUERY_TYPE));
		String queryText = intent
				.getStringExtra(DiscogsConstants.KEY_QUERY_TEXT);

		if (queryText == null || queryType == null)
			finish();

		this.listArtists(queryType, queryText);

		// Click event for single list row
		list.setOnItemClickListener(this);
	}

	@Override
	public boolean onSearchRequested() {
		Log.i(TAG, "Search requested");
		return super.onSearchRequested();
	}

	@UiThread
	protected void updateListView(SearchResult data) {
		if (loadingProgressBar.getVisibility() == View.VISIBLE) {
			loadingProgressBar.setVisibility(View.INVISIBLE);
		}
		
		List<Artist> artistsList = new ArrayList<Artist>();

		if (data.getCount() <= 0) {
			Toast.makeText(this, "No singer was found", Toast.LENGTH_SHORT)
					.show();
			this.finish();
			return;
		}

		currentSearchResult = data;
		artistsList = this.getArtistsFromSearchResult(currentSearchResult);
		list = (ListView) findViewById(android.R.id.list);

		// If the adapter doesn't exist, we create one with the initial data.
		// If it exists, we update it.
		int currentPosition = -1;
		if (adapter == null) {
			adapter = new ArtistsListAdapter(this, artistsList);
		} else {
			currentPosition = list.getFirstVisiblePosition();
			adapter.addItems(artistsList);
		}
		list.setAdapter(adapter);

		list.setSelectionFromTop(currentPosition + 1, 0);

		if (currentSearchResult.getPagination().isLast()) {
			list.removeFooterView(loadMoreProgress);
		}
	}

	private List<Artist> getArtistsFromSearchResult(SearchResult items) {
		ArrayList<Artist> artistsList = new ArrayList<Artist>();

		for (int i = 0; i < items.getCount(); i++) {
			SearchItem item = items.getResults().get(i);
			Artist artist = new Artist(item.getId(), item.getTitle(), null);
			artist.getImages().add(new Image(item.getThumbURL()));
			artistsList.add(artist);
		}

		return artistsList;
	}

	@Background
	protected void listArtists(QueryType queryType, String query) {
		DiscogsService discogsService = new DiscogsService(this, null);
		SearchResult searchResult = new SearchResult();
		
		try {
			searchResult = discogsService.search(queryType, query);
		} catch (NoInternetConnectionException e) {
			showNoInternetMessage();
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			showLazyInternetMessage();
			e.printStackTrace();
		}
		updateListView(searchResult);
	}

	@Background
	protected void loadMore() {
		SearchResult searchResult = null;
		try {
			DiscogsService service = new DiscogsService(
					ArtistsListActivity.this, discogsCustomer);
			searchResult = service.next(currentSearchResult.getPagination());
		} catch (NoInternetConnectionException e) {
			showNoInternetMessage();
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			showLazyInternetMessage();
			e.printStackTrace();
		}
		updateListView(searchResult);
	}

	/**
	 * OnScrollListener
	 */
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (currentSearchResult == null) {
			return;
		}

		Log.i(TAG, firstVisibleItem + "/" + totalItemCount);
		boolean isLast = firstVisibleItem + visibleItemCount == totalItemCount;
		if (isLast && !currentSearchResult.getPagination().isLast()
				&& !this.isLoading) {
			this.loadMore();
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.i(TAG, "Scroll state=" + scrollState);
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
