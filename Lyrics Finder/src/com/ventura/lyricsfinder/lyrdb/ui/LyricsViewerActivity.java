package com.ventura.lyricsfinder.lyrdb.ui;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.ventura.lyricsfinder.GlobalConstants;
import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.lyrdb.LyrDBService;
import com.ventura.lyricsfinder.lyrdb.QueryType;
import com.ventura.lyricsfinder.lyrdb.entities.Lyric;

public class LyricsViewerActivity extends Activity {
	
	TextView lyricsTextView;
	TextView artistNameTextView;
	TextView musicNameTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lyric_view);
		
		Intent intent = this.getIntent();
		String lyricId = intent.getStringExtra(GlobalConstants.EXTRA_LYRIC_ID);
		
		lyricsTextView = (TextView) this.findViewById(R.id.lyrics_text_view);
		artistNameTextView = (TextView) this.findViewById(R.id.artist_text_view);
		musicNameTextView = (TextView) this.findViewById(R.id.music_text_view);

		new GetLyricTask(this).execute(lyricId);
		artistNameTextView.setText(intent.getStringExtra(GlobalConstants.EXTRA_ARTIST_NAME));
		musicNameTextView.setText(intent.getStringExtra(GlobalConstants.EXTRA_TRACK_NAME));
	}
	
	private void setLyric(String lyric) {
		lyricsTextView.setText(lyric);
	}
	
	private class GetLyricTask extends AsyncTask<String, Void, String> {

		private Context mContext;
		private ProgressDialog mProgressDialog;

		public GetLyricTask(Context context) {
			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_lyric_title));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_lyric_body));
			this.mProgressDialog.setCancelable(true);

			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//this.mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String lyricId = params[0];
			LyrDBService lyricsService = new LyrDBService(this.mContext);
			return lyricsService.getLyric(lyricId);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			setLyric(result);
			//this.mProgressDialog.dismiss();
		}
	}
}
