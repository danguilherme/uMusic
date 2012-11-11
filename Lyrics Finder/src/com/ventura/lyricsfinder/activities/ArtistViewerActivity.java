package com.ventura.lyricsfinder.activities;

import org.json.JSONException;

import oauth.signpost.OAuthConsumer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.activities.util.ImageLoader;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.entities.Artist;

public class ArtistViewerActivity extends BaseActivity {

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

		Artist artist = new Artist();
		try {
			artist = new DiscogsService(this).getArtistInfo(artistId,
					consumer);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (artist.getImages().size() > 0) {
			new ImageLoader(this).DisplayImage(artist.getImages().get(0)
					.getUri().toString(), artistImage);
		} else {
			artistImage.setVisibility(View.INVISIBLE);
		}
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
}
