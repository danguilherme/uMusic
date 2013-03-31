package com.ventura.musicexplorer.ui.artist;

import java.util.ArrayList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.androidutils.utils.ConnectionManager;
import com.ventura.androidutils.utils.InnerActivityAsyncTask;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.discogs.DiscogsConstants;
import com.ventura.musicexplorer.discogs.DiscogsService;
import com.ventura.musicexplorer.discogs.OnContentDownloadListener;
import com.ventura.musicexplorer.discogs.entity.SearchItem;
import com.ventura.musicexplorer.discogs.entity.SearchResult;
import com.ventura.musicexplorer.discogs.entity.enumerator.QueryType;
import com.ventura.musicexplorer.entity.Image;
import com.ventura.musicexplorer.entity.artist.Artist;

public class ListArtistsActivity extends SherlockListActivity implements
		OnScrollListener, OnItemClickListener {
	final String TAG = getClass().getName();

	private SharedPreferences prefs;

	private ListView list;
	private ArtistsListAdapter adapter;
	private OAuthConsumer discogsCustomer;
	private SearchResult currentSearchResult;
	private LinearLayout loadMoreProgress;
	boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final LinearLayout mainLayout = (LinearLayout) this.getLayoutInflater()
				.inflate(R.layout.default_list, null);
		this.setContentView(mainLayout);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		discogsCustomer = new ArtistViewerActivity().getConsumer(this.prefs);

		Intent intent = this.getIntent();

		list = (ListView) findViewById(android.R.id.list);
		list.setOnScrollListener(this);

		loadMoreProgress = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.load_more_progress, null);
		list.addFooterView(loadMoreProgress);

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

		new ListArtistsTask(this, discogsCustomer, queryType)
				.execute(queryText);

		// Click event for single list row
		list.setOnItemClickListener(this);
	}

	@Override
	public boolean onSearchRequested() {
		Log.i(TAG, "Search requested");
		return super.onSearchRequested();
	}

	private void updateListView(SearchResult data) {
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
			adapter.add(artistsList);
		}
		adapter.notifyDataSetChanged();
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

	private void loadMore() {
		this.isLoading = true;
		OnContentDownloadListener listener = new OnContentDownloadListener() {
			public void onDownloadFinished(Object result) {
				updateListView((SearchResult) result);
				isLoading = false;
			}

			public void onDownloadError(String error) {
				isLoading = false;
			}
		};
		new LoadMoreTask(listener).execute();
	}

	private class ListArtistsTask extends
			InnerActivityAsyncTask<String, Void, SearchResult> {
		private OAuthConsumer mConsumer;
		private QueryType mQueryType;

		public ListArtistsTask(Context context, OAuthConsumer consumer,
				QueryType queryType) {
			super(context, getString(R.string.app_name),
					getString(R.string.message_fetching_artists_list));
			this.mConsumer = consumer;
			this.mQueryType = queryType;
		}

		@Override
		protected SearchResult doInBackground(String... params) {
			DiscogsService discogsService = new DiscogsService(
					this.getContext(), this.mConsumer);
			// ArtistService artistService = new
			// ArtistService(this.getContext());
			try {
				return discogsService.search(this.mQueryType, params[0]);
			} catch (NoInternetConnectionException e) {
				/*
				 * Toast.makeText(mContext, "No internet connection...",
				 * Toast.LENGTH_SHORT).show();
				 */
				e.printStackTrace();
			} catch (LazyInternetConnectionException e) {
				/*
				 * Toast.makeText(mContext,
				 * "Your connection is lazy! Try again?",
				 * Toast.LENGTH_SHORT).show();
				 */
				e.printStackTrace();
			}
			// finish();
			return new SearchResult();
		}

		@Override
		protected void onPostExecute(SearchResult result) {
			super.onPostExecute(result);
			updateListView(result);
		}

		@Override
		public void onProgressDialogCancelled(DialogInterface progressDialog) {
		}
	}

	private class LoadMoreTask extends AsyncTask<Void, Void, SearchResult> {
		OnContentDownloadListener contentDownloadListener;

		public LoadMoreTask(OnContentDownloadListener contentDownloadListener) {
			this.contentDownloadListener = contentDownloadListener;
		}

		@Override
		protected SearchResult doInBackground(Void... params) {
			try {
				DiscogsService service = new DiscogsService(
						ListArtistsActivity.this, discogsCustomer);
				SearchResult searchResult = service.next(currentSearchResult
						.getPagination());
				return searchResult;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchResult searchResult) {
			if (this.contentDownloadListener != null) {
				contentDownloadListener.onDownloadFinished(searchResult);
			}
		}
	}

	// OnScrollListener
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
