package com.ventura.lyricsfinder.ui;

import java.io.IOException;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.TrackData;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.musixmatch.ui.Constants;

public class BindLyricsActivity extends BaseActivity {

	final String TAG = getClass().getName();

	private Button mAcceptLyricsButton;
	private Button mEditLyricsButton;
	private TextView mLyricsTextView;
	private EditText mLyricsTextField;
	private MP3File mActualMP3File;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_bind_lyrics);
		mAcceptLyricsButton = (Button) this
				.findViewById(R.id.btn_accept_lyrics);
		mEditLyricsButton = (Button) this.findViewById(R.id.btn_edit_lyrics);
		mLyricsTextView = (TextView) this.findViewById(R.id.lyrics_text_view);
		mLyricsTextField = (EditText) this.findViewById(R.id.lyrics_text_field);

		Intent intent = getIntent();
		String action = intent.getAction();

		if (!action.equals(Intent.ACTION_SEND)) {
			finish();
		}

		Uri sharedPath = intent.getData();
		if (sharedPath == null && intent.getExtras() != null) {
			sharedPath = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
		}

		try {
			mActualMP3File = new MP3File(this.getSharedFile(sharedPath));
		} catch (IOException e) {
			Log.i(TAG, "IOException - Error instantiating MP3File from path "
					+ sharedPath + ". Error message: " + e.getMessage());
		} catch (TagException e) {
			Log.i(TAG, "TagException - Error instantiating MP3File from path "
					+ sharedPath + ". Error message: " + e.getMessage());
		}

		if (!mActualMP3File.hasID3v2Tag()) {
			Toast.makeText(getBaseContext(),
					getString(R.string.message_file_not_supported),
					Toast.LENGTH_SHORT).show();
			finish();
		}

		if (mActualMP3File.getID3v2Tag().getSongLyric().length() > 0) {
			Toast.makeText(getBaseContext(), "This song already have lyrics.",
					Toast.LENGTH_SHORT).show();
		}

		new LyricsSearchTask(this).execute(mActualMP3File);

		mAcceptLyricsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String lyrics = null;
				if (mLyricsTextView.getVisibility() == View.VISIBLE) {
					lyrics = mLyricsTextView.getText().toString();
				} else {
					lyrics = mLyricsTextField.getText().toString();
				}
				mActualMP3File.getID3v2Tag().setSongLyric(lyrics);

				try {
					mActualMP3File.save(TagConstant.MP3_FILE_SAVE_OVERWRITE);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (TagException e) {
					e.printStackTrace();
				}

				Toast.makeText(getBaseContext(),
						getString(R.string.message_lyrics_saved),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		mEditLyricsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mEditLyricsButton.setVisibility(View.GONE);
				mLyricsTextView.setVisibility(View.GONE);
				mLyricsTextField.setVisibility(View.VISIBLE);

				mLyricsTextField.setText(mLyricsTextView.getText());
			}
		});
	}

	private void setLyrics(Lyrics lyrics) {
		Log.i(TAG, "Found Lyrics: \r\n" + lyrics.getLyricsBody());
		mLyricsTextView.setText(lyrics.getLyricsBody());
		mAcceptLyricsButton.setVisibility(View.VISIBLE);
		mEditLyricsButton.setVisibility(View.VISIBLE);
	}

	private class LyricsSearchTask extends AsyncTask<MP3File, Void, Lyrics> {

		private ProgressDialog mProgressDialog;
		private Context mContext;

		public LyricsSearchTask(Context context) {
			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			this.mProgressDialog = new ProgressDialog(mContext);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_lyric_title));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_lyric_body));
			this.mProgressDialog.setCancelable(true);
			this.mProgressDialog.show();
		}

		@Override
		protected Lyrics doInBackground(MP3File... params) {
			MP3File targetFile = params[0];
			MusixMatch musixMatch = new MusixMatch(Constants.API_KEY);
			String artist = null, musicName = null;
			if (targetFile.hasID3v2Tag()) {
				artist = targetFile.getID3v2Tag().getLeadArtist();
				musicName = targetFile.getID3v2Tag().getSongTitle();
			} else if (targetFile.hasID3v1Tag()) {
				artist = targetFile.getID3v1Tag().getLeadArtist();
				musicName = targetFile.getID3v1Tag().getSongTitle();
			}
			TrackData track = null;
			Lyrics lyrics = null;
			try {
				track = musixMatch.getMatchingTrack(musicName, artist)
						.getTrack();
				lyrics = musixMatch.getLyrics(track.getTrackId());
			} catch (MusixMatchException e) {
				// Toast.makeText(mContext,
				// getString(R.string.message_lyric_not_found),
				// Toast.LENGTH_SHORT).show();
				// finish();
			}

			return lyrics;
		}

		@Override
		protected void onPostExecute(Lyrics result) {
			mProgressDialog.dismiss();
			if (result != null) {
				super.onPostExecute(result);
				setLyrics(result);
			} else {
				Toast.makeText(mContext,
						getString(R.string.message_lyric_not_found),
						Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
}
