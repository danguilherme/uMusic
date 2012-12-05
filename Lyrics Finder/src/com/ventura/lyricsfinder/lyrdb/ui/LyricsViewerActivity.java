package com.ventura.lyricsfinder.lyrdb.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;
import com.ventura.lyricsfinder.lyrdb.LyrDBService;

public class LyricsViewerActivity extends Activity {
	
	private TextView lyricsTextView;
	private TextView artistNameTextView;
	private TextView musicNameTextView;
	private Button acceptLyricsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lyric_view);

		Intent intent = this.getIntent();
		String lyricId = intent.getStringExtra(GlobalConstants.EXTRA_LYRIC_ID);

		lyricsTextView = (TextView) this.findViewById(R.id.lyrics_text_view);
		artistNameTextView = (TextView) this
				.findViewById(R.id.artist_text_view);
		musicNameTextView = (TextView) this.findViewById(R.id.music_text_view);
		acceptLyricsButton = (Button) this.findViewById(R.id.btn_accept_lyrics);

		new GetLyricTask(this).execute(lyricId);
		artistNameTextView.setText(intent
				.getStringExtra(GlobalConstants.EXTRA_ARTIST_NAME));
		musicNameTextView.setText(intent
				.getStringExtra(GlobalConstants.EXTRA_TRACK_NAME));
		
		acceptLyricsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendResponse(lyricsTextView.getText());
			}
		});
	}
	
	private void sendResponse(CharSequence lyrics) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(GlobalConstants.EXTRA_TRACK_LYRICS, lyrics);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	private void setLyric(String lyric) {
		lyricsTextView.setText(lyric);
		acceptLyricsButton.setVisibility(View.VISIBLE);
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
			// this.mProgressDialog.show();
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
			// this.mProgressDialog.dismiss();
		}
	}
}
