package com.ventura.umusic.ui.music;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.umusic.R;
import com.ventura.umusic.business.LyricsService;
import com.ventura.umusic.constants.GlobalConstants;
import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.entity.music.Track;
import com.ventura.umusic.music.TracksManager;
import com.ventura.umusic.ui.BaseListActivity;

@EActivity(R.layout.activity_lyrics_view)
public class LyricsViewerActivity extends BaseListActivity implements
		OnItemClickListener {
	final String TAG = getClass().getName();

	@ViewById(R.id.artist_name)
	EditText artistName;

	@ViewById(R.id.song_name)
	EditText songName;

	@ViewById(R.id.search_lyrics)
	Button searchLyrics;

	@ViewById(R.id.lyrics_matching_params)
	LinearLayout lyricsMatchingParamsContainer;

	@AnimationRes(R.anim.slide_in_down)
	Animation slideDown;

	@AnimationRes(R.anim.slide_out_up)
	Animation slideUp;

	@ViewById(android.R.id.list)
	protected ListView strophesList;

	Lyrics mLyric;
	private StrophesAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// This has to be called before setContentView and you must use the
		// class in com.actionbarsherlock.view and NOT android.view
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	}

	public String getArtistName() {
		return artistName.getText().toString();
	}

	public void setArtistName(String artistName) {
		this.artistName.setText(artistName);
	}

	public String getSongName() {
		return songName.getText().toString();
	}

	public void setSongName(String songName) {
		this.songName.setText(songName);
	}

	@AfterViews
	public void onAfterViews() {
		strophesList.setOnItemClickListener(this);
		fetchLyricsInfo();
		if (getSongName() == "" || getArtistName() == "") {
			finish();
			return;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(getSongName());
		actionBar.setSubtitle(getArtistName());

		fetchLyrics();
	}

	/**
	 * Responsible to get artist and song names from wherever they comes in the
	 * Intent.
	 */
	private void fetchLyricsInfo() {
		Intent intent = this.getIntent();
		String action = intent.getAction();
		Bundle extras = intent.getExtras();

		if (Intent.ACTION_SEND.equals(action)) {
			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

			TracksManager tracksManager = new TracksManager(this);
			Track track = tracksManager.getTrackByUri(uri.toString());

			if (track != null) {
				setArtistName(track.getArtist().getName());
				setSongName(track.getTitle());
			}
		} else {
			setArtistName(extras.getString(GlobalConstants.EXTRA_ARTIST_NAME));
			setSongName(extras.getString(GlobalConstants.EXTRA_TRACK_NAME));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_lyrics_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_share:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("plain/text");
			Log.d(TAG, "Sharing lyrics");
			// Log.d(TAG, selectedText.equals("") ? mLyric.getLyricsText()
			// : selectedText);
			// shareIntent.putExtra(Intent.EXTRA_TEXT,
			// selectedText.equals("") ? mLyric.getLyricsText()
			// : selectedText);
			startActivity(shareIntent);
			break;
		case R.id.action_set_search_params:
			item.setChecked(!item.isChecked());
			toggleLyricsSearchParamsVisibility();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Click(R.id.search_lyrics)
	protected void onSearchLyricsButtonClick() {
		toggleLyricsSearchParamsVisibility();
		fetchLyrics();
	}

	/**
	 * Gets the lyrics from internet
	 */
	@Background
	protected void fetchLyrics() {
		showIndeterminateProgress(true);
		LyricsService lyricsService = new LyricsService(this);

		try {
			Lyrics lyrics = lyricsService.getLyrics(getArtistName(),
					getSongName());
			if (lyrics == null)
				Log.i(TAG, "Lyrics not found");
			else
				Log.i(TAG, "Lyrics found: " + lyrics.getLyricsText());

			updateView(lyrics);
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
			alert(e.getMessage());
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
			alert(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			alert(e.getMessage());
		} finally {
			showIndeterminateProgress(false);
		}
	}

	@UiThread
	public void updateView(Lyrics lyric) {
		this.mLyric = lyric;

		if (lyric == null || lyric.getLyricsText() == null
				|| lyric.getLyricsText().equals("")) {
			TextView textView = (TextView) getLayoutInflater().inflate(
					R.layout.list_item_lyrics_strophe, null);
			textView.setTextIsSelectable(false);
			textView.setText(getResources().getString(
					R.string.message_lyric_not_found));
		} else
			updateStrophes();
	}

	private void updateStrophes() {
		if (listAdapter == null) {
			listAdapter = new StrophesAdapter(this, mLyric.getLyricsStrophes());
			strophesList.setAdapter(listAdapter);
		} else {
			listAdapter.addItems(mLyric.getLyricsStrophes());
			listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * The artist that came from search params, to compare when closing the
	 * search params container and re-searching (or not) the lyrics
	 */
	private String originalArtist = "";
	/**
	 * Same as {@link originalArtist}, but holds the song
	 */
	private String originalSong = "";

	@UiThread
	protected void toggleLyricsSearchParamsVisibility() {
		// if hidden
		if (lyricsMatchingParamsContainer.getVisibility() == View.GONE) {
			originalArtist = getArtistName();
			originalSong = getSongName();
			lyricsMatchingParamsContainer.setVisibility(View.VISIBLE);
			lyricsMatchingParamsContainer.startAnimation(slideDown);
		} else {
			lyricsMatchingParamsContainer.startAnimation(slideUp);

			slideUp.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					Log.d(TAG, "Animation end: slideUp");
					lyricsMatchingParamsContainer.setVisibility(View.GONE);
					Log.d(TAG, String.format(
							"Lyrics params view visibility is %1$d",
							lyricsMatchingParamsContainer.getVisibility()));
				}
			});

			if ((getArtistName() != null && !getArtistName().equals(
					originalArtist))
					|| (getSongName() != null && !getSongName().equals(
							originalSong))) {
				fetchLyrics();
			}
		}

		Log.d(TAG, String.format("Lyrics params view visibility is %1$d",
				lyricsMatchingParamsContainer.getVisibility()));
	}

	@UiThread
	protected void showIndeterminateProgress(boolean show) {
		setSupportProgressBarIndeterminateVisibility(show);
	}

	// onItemClickListener methods
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		SparseBooleanArray checked = strophesList.getCheckedItemPositions();

		for (int i = 0; i < checked.size(); i++) {
			int pos = checked.keyAt(i);
			listAdapter.setChecked(pos, checked.get(i));

			Log.d(TAG,
					"POS=" + i + " - isChecked="
							+ listAdapter.isChecked(i));
		}

		View listItem = listAdapter.getView(position, v, null);
		listItem.setBackgroundColor(listAdapter.isChecked(position) ? this
				.getResources().getColor(R.color.red) : this.getResources()
				.getColor(R.color.transparent));

	}

}