package com.ventura.lyricsfinder.lyrdb.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;
import com.ventura.lyricsfinder.constants.RequestCodes;
import com.ventura.lyricsfinder.lyrdb.CustomAdapter;
import com.ventura.lyricsfinder.lyrdb.LyrDBService;
import com.ventura.lyricsfinder.lyrdb.QueryType;
import com.ventura.lyricsfinder.lyrdb.entities.Lyric;

public class ListLyricsActivity extends ListActivity {
	final String TAG = getClass().getName();

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
				intent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME, mListItems
						.get(position).getArtistName());
				intent.putExtra(GlobalConstants.EXTRA_TRACK_NAME, mListItems
						.get(position).getMusicName());
				startActivityForResult(intent, RequestCodes.GET_LYRICS);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RequestCodes.GET_LYRICS) {
			if (resultCode == RESULT_OK) {
				this.sendResult(data
						.getStringExtra(GlobalConstants.EXTRA_TRACK_LYRICS));
			}
			if (resultCode == RESULT_CANCELED) {
				setResult(RESULT_CANCELED, new Intent());
			}
		}
	}

	private void sendResult(CharSequence lyrics) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(GlobalConstants.EXTRA_TRACK_LYRICS, lyrics);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	private void fillListView(List<Lyric> lyrics) {
		if (lyrics.size() <= 0) {
			Toast.makeText(this, "No lyrics were found", Toast.LENGTH_SHORT)
					.show();
			this.finish();
		}

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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i(TAG, "LANDSCAPE");
		} else {
			Log.i(TAG, "PORTRAIT");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = new MenuInflater(this);
		mi.inflate(R.menu.list_lyrics_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_set_music_tags) {
			Log.i(TAG, item.getTitle() + " clicked");
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	private class ListLyricsTask extends AsyncTask<Lyric, Void, List<Lyric>> {

		private Context mContext;
		private QueryType mQueryType;
		private ProgressDialog mProgressDialog;

		public ListLyricsTask(Context context, QueryType queryType) {
			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_lyrics_list_body));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_lyrics_list_body));
			this.mProgressDialog.setCancelable(true);

			this.mContext = context;
			this.mQueryType = queryType;
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
			return lyricsService.search(this.mQueryType, target);
		}

		@Override
		protected void onPostExecute(List<Lyric> result) {
			super.onPostExecute(result);
			fillListView(result);
			this.mProgressDialog.dismiss();
		}
	}
}
