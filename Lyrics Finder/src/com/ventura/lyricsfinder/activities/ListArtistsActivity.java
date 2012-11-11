package com.ventura.lyricsfinder.activities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.activities.util.LazyAdapter;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.entities.SearchItem;
import com.ventura.lyricsfinder.discogs.entities.SearchResult;
import com.ventura.lyricsfinder.discogs.entities.QueryType;

public class ListArtistsActivity extends ListActivity {
	private SharedPreferences prefs;

	ListView list;
	LazyAdapter adapter;
	ProgressDialog mProgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.artists_list);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		mProgDialog = new ProgressDialog(this);
		mProgDialog.setTitle(getString(R.string.message_fetching_lyric_title));
		mProgDialog.setMessage(getString(R.string.message_fetching_lyric_body));
		mProgDialog.setCancelable(true);
		mProgDialog.show();

		Intent intent = this.getIntent();
		QueryType queryType = Enum.valueOf(QueryType.class,
				intent.getStringExtra(DiscogsConstants.KEY_QUERY_TYPE));
		String queryText = intent
				.getStringExtra(DiscogsConstants.KEY_QUERY_TEXT);

		if (queryText == null || queryType == null)
			finish();

		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

		try {
			SearchResult search = new DiscogsService(this).search(queryType,
					queryText,
					new ArtistViewerActivity().getConsumer(this.prefs));
			if (search.getCount() <= 0) {
				Toast.makeText(this, "No singer was found", Toast.LENGTH_SHORT)
						.show();
				this.finish();
			} else {
				for (int i = 0; i < search.getCount(); i++) {

					HashMap<String, String> map = new HashMap<String, String>();
					SearchItem item = search.getResults().get(i);
					map.put(DiscogsConstants.KEY_ID,
							String.valueOf(item.getArtist().getId()));
					map.put(DiscogsConstants.KEY_TITLE, item.getArtist()
							.getName()); // Song
					// title
					map.put(DiscogsConstants.KEY_TITLE, item.getArtist()
							.getName()); // Song
											// artist
					map.put(DiscogsConstants.KEY_ID,
							String.valueOf(item.getArtist().getId()));
					map.put(DiscogsConstants.KEY_THUMB, item.getArtist()
							.getImages().get(0).getUri().toString());

					songsList.add(map);
				}
			}
			// clearCredentials.setText(results);
			mProgDialog.dismiss();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		list = (ListView) findViewById(android.R.id.list);

		// Getting adapter by passing xml data ArrayList
		adapter = new LazyAdapter(this, songsList);
		list.setAdapter(adapter);

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

	@Override
	protected void onResume() {
		super.onResume();
		// this.mProgDialog.show();
	}
}
