package com.ventura.lyricsfinder.discogs.ui;

import java.util.ArrayList;
import java.util.HashMap;

import oauth.signpost.OAuthConsumer;

import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.LazyAdapter;
import com.ventura.lyricsfinder.discogs.entities.QueryType;
import com.ventura.lyricsfinder.discogs.entities.SearchItem;
import com.ventura.lyricsfinder.discogs.entities.SearchResult;

public class ListArtistsActivity extends ListActivity {
	private SharedPreferences prefs;

	ListView list;
	LazyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final LinearLayout mainLayout = (LinearLayout) this.getLayoutInflater()
				.inflate(R.layout.default_list, null);
		this.setContentView(mainLayout);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Intent intent = this.getIntent();
		QueryType queryType = Enum.valueOf(QueryType.class,
				intent.getStringExtra(DiscogsConstants.KEY_QUERY_TYPE));
		String queryText = intent
				.getStringExtra(DiscogsConstants.KEY_QUERY_TEXT);

		if (queryText == null || queryType == null)
			finish();

		new ListArtistsTask(this,
				new ArtistViewerActivity().getConsumer(this.prefs), queryType).execute(queryText);

		list = (ListView) findViewById(android.R.id.list);

		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(view.getContext(),
						ArtistViewerActivity.class);

				intent.setAction(Intent.ACTION_SEND);
				intent.putExtra(DiscogsConstants.KEY_ID,
						adapter.getId(position));
				startActivity(intent);
			}
		});
	}

	private void fillListView(SearchResult data) {
		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

		if (data.getCount() <= 0) {
			Toast.makeText(this, "No singer was found", Toast.LENGTH_SHORT)
					.show();
			this.finish();
		} else {
			for (int i = 0; i < data.getCount(); i++) {

				HashMap<String, String> map = new HashMap<String, String>();
				SearchItem item = data.getResults().get(i);
				map.put(DiscogsConstants.KEY_ID,
						String.valueOf(item.getArtist().getId()));
				map.put(DiscogsConstants.KEY_TITLE, item.getArtist().getName()); // Song
				// title
				map.put(DiscogsConstants.KEY_TITLE, item.getArtist().getName()); // Song
																					// artist
				map.put(DiscogsConstants.KEY_ID,
						String.valueOf(item.getArtist().getId()));
				map.put(DiscogsConstants.KEY_THUMB, item.getArtist()
						.getImages().get(0).getUrl().toString());

				songsList.add(map);
			}
		}
		list = (ListView) findViewById(android.R.id.list);

		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(this, songsList);
		list.setAdapter(adapter);
	}

	private class ListArtistsTask extends AsyncTask<String, Void, SearchResult> {
		private ProgressDialog mProgressDialog;
		private Context mContext;
		private OAuthConsumer mConsumer;
		private QueryType mQueryType;

		public ListArtistsTask(Context context, OAuthConsumer consumer,
				QueryType queryType) {

			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_lyric_title));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_lyric_body));
			this.mProgressDialog.setCancelable(true);
			this.mContext = context;
			this.mConsumer = consumer;
			this.mQueryType = queryType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.mProgressDialog.show();
		}

		@Override
		protected SearchResult doInBackground(String... params) {
			DiscogsService discogsService = new DiscogsService(this.mContext);
			try {
				return discogsService.search(this.mQueryType, params[0],
						this.mConsumer);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(SearchResult result) {
			super.onPostExecute(result);
			this.mProgressDialog.dismiss();
			fillListView(result);
		}
	}
}
