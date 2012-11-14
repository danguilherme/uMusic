package com.ventura.lyricsfinder.lyrdb.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ventura.lyricsfinder.GlobalConstants;
import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.lyrdb.CustomAdapter;
import com.ventura.lyricsfinder.lyrdb.LyrDBService;
import com.ventura.lyricsfinder.lyrdb.QueryType;
import com.ventura.lyricsfinder.lyrdb.entities.Lyric;

public class ListLyricsActivity extends ListActivity {
	
	private SharedPreferences prefs;
	ListView list;
	CustomAdapter adapter;
	List<Lyric> mListItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.default_list);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Intent intent = this.getIntent();
		String artist = intent
				.getStringExtra(GlobalConstants.EXTRA_ARTIST_NAME);
		String music = intent.getStringExtra(GlobalConstants.EXTRA_TRACK_NAME);

		list = (ListView) findViewById(android.R.id.list);

		new ListLyricsTask(this, QueryType.FullT).execute(new Lyric(null,
				music, artist));

		// Click event for single list row
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(view.getContext(),
						LyricsViewerActivity.class);

				intent.setAction(Intent.ACTION_SEND);
				intent.putExtra(GlobalConstants.EXTRA_LYRIC_ID,
						mListItems.get(position).getId());
				intent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME,
						mListItems.get(position).getArtistName());
				intent.putExtra(GlobalConstants.EXTRA_TRACK_NAME,
						mListItems.get(position).getMusicName());
				startActivity(intent);
			}
		});
	}

	private void fillListView(List<Lyric> lyrics) {
		ArrayList<HashMap<String, String>> lyricsList = new ArrayList<HashMap<String, String>>();

		for (int i = 0; i < lyrics.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			Lyric item = lyrics.get(i);
			map.put(CustomAdapter.MUSIC_NAME, item.getMusicName());
			map.put(CustomAdapter.ARTIST_NAME, item.getArtistName());

			lyricsList.add(map);
		}

		// Getting adapter by passing xml data ArrayList
		adapter = new CustomAdapter(this, lyricsList);
		list.setAdapter(adapter);
		this.mListItems = lyrics;
	}

	private class ListLyricsTask extends AsyncTask<Lyric, Void, List<Lyric>> {

		private Context mContext;
		private QueryType mQueryTyoe;
		private ProgressDialog mProgressDialog;

		public ListLyricsTask(Context context, QueryType queryType) {
			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_lyric_title));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_lyric_body));
			this.mProgressDialog.setCancelable(true);

			this.mContext = context;
			this.mQueryTyoe = queryType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.mProgressDialog.show();
		}

		@Override
		protected List<Lyric> doInBackground(Lyric... params) {
			Lyric target = params[0];
			LyrDBService lyricsService = new LyrDBService(this.mContext);
			return lyricsService.search(this.mQueryTyoe,
					target.getArtistName(), target.getMusicName());
		}

		@Override
		protected void onPostExecute(List<Lyric> result) {
			super.onPostExecute(result);
			fillListView(result);
			this.mProgressDialog.dismiss();
		}
	}
}
