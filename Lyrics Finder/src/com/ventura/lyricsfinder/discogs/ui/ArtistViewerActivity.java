package com.ventura.lyricsfinder.discogs.ui;

import java.net.URL;
import java.util.List;

import oauth.signpost.OAuthConsumer;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.ImageLoaderTask;
import com.ventura.lyricsfinder.discogs.entities.Artist;
import com.ventura.lyricsfinder.discogs.entities.Image;
import com.ventura.lyricsfinder.lyrdb.LyrDBService;
import com.ventura.lyricsfinder.lyrdb.QueryType;
import com.ventura.lyricsfinder.lyrdb.entities.Lyric;
import com.ventura.lyricsfinder.ui.BaseActivity;
import com.ventura.lyricsfinder.util.ImageLoader;

public class ArtistViewerActivity extends BaseActivity {
	/*
	private class ArtistInfoDownloadTask extends AsyncTask<URL, Void, Artist>
	{
		private Context mContext;
		private QueryType mQueryTyoe;
		private ProgressDialog mProgressDialog;

		public ListLyricsTask(Context context, QueryType queryType) {
			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_lyrics_list_body));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_lyrics_list_body));
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
		protected Artist doInBackground(URL... params) {
			URL target = params[0];
			LyrDBService lyricsService = new LyrDBService(this.mContext);
			return lyricsService.search();
		}

		@Override
		protected void onPostExecute(Artist result) {
			super.onPostExecute(result);
			fillArtistInfo(result);
			this.mProgressDialog.dismiss();
		}
	}*/

	final String TAG = getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.artist_info);

		Intent intent = this.getIntent();
		String artistId = intent.getStringExtra(DiscogsConstants.KEY_ID);
		OAuthConsumer consumer = this.getConsumer(this.prefs);

		ImageView artistImage = (ImageView) findViewById(R.id.artist_image);
		TextView artistBio = (TextView) findViewById(R.id.artist_bio);
		TextView artistName = (TextView) findViewById(R.id.artist_name);
		TextView artistDiscogsLink = (TextView) findViewById(R.id.artist_discogs_url);

		Artist artist = new Artist();
		try {
			artist = new DiscogsService(this).getArtistInfo(artistId, consumer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (artist.getImages().size() > 0) {
			Image firstImage = artist.getImages().get(0);
			new ImageLoaderTask(artistImage, firstImage).execute();
			new ImageLoader(this).DisplayImage(firstImage.getUrl().toString(),
					artistImage);
			if (firstImage.getHeight() > 0 && firstImage.getWidth() > 0) {
				artistImage.setMinimumHeight(firstImage.getHeight());
				artistImage.setMinimumWidth(firstImage.getWidth());
			}
		} else {
			artistImage.setVisibility(View.INVISIBLE);
		}
		artistName.setText(artist.getName());
		artistDiscogsLink.setText(Html.fromHtml("<a href=\""
				+ artist.getDiscogsUrl().toString() + "\">"
				+ getString(R.string.discogs_view_artist_profile) + "</a>"));
		artistDiscogsLink.setMovementMethod(LinkMovementMethod.getInstance());
		String profile = artist.getProfile();

		if (profile != null && !profile.equals("")) {
			artistBio.setText(profile);
		} else {
			artistBio.setText("This artist has no Bio. Add one!");
		}
	}

	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		/*
		 * switch (reqCode) { case (PICK_CONTACT): if (resultCode ==
		 * Activity.RESULT_OK) { Uri contactData = data.getData(); Cursor c =
		 * managedQuery(contactData, null, null, null, null); if
		 * (c.moveToFirst()) { String name = c.getString(c
		 * .getColumnIndexOrThrow(People.NAME)); Log.i(TAG, "Response : " +
		 * "Selected contact : " + name); } } break; }
		 */
	}
	
	private void fillArtistInfo(Artist artist) {
		
	}
}
