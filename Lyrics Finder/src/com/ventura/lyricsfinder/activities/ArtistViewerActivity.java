package com.ventura.lyricsfinder.activities;

import oauth.signpost.OAuthConsumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.activities.util.ImageLoader;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;

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
		WebView artistBioWebView = (WebView) findViewById(R.id.webview);

		try {
			JSONObject artistInfo = new DiscogsService(this).getArtistInfo(
					artistId, consumer);
			
			JSONArray images = artistInfo.optJSONArray("images");
			if (images != null && images.length() > 0) {
				JSONObject firstImage = images.getJSONObject(0);
				new ImageLoader(this)
						.DisplayImage(firstImage
								.getString(DiscogsConstants.KEY_RESOURCE_URL),
								artistImage);
			} else {
				artistImage.setVisibility(View.INVISIBLE);
			}
			String profile = artistInfo.optString("profile");

			if (profile != null && !profile.equals("")) {
				String text = "<html><body>" + "<p align=\"justify\">"
						+ profile + "</p> " + "</body></html>";
				artistBioWebView.loadData(text, "text/html", "utf-8");
				artistBio.setText(profile);
			} else {
				artistBio.setText("This artist has no Bio. Add one!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Object getJSONValue(JSONObject target, String key)
			throws JSONException {
		if (!target.isNull(key)) {
			return target.get(key);
		}
		return null;
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
