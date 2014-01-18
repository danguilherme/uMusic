package com.ventura.umusic.ui;

import java.io.File;
import java.io.IOException;

import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.androidutils.utils.ConnectionManager;
import com.ventura.androidutils.utils.Utils;
import com.ventura.umusic.R;
import com.ventura.umusic.constants.GlobalConstants;
import com.ventura.umusic.discogs.DiscogsConstants;
import com.ventura.umusic.discogs.entity.enumerator.QueryType;
import com.ventura.umusic.entity.artist.Artist;
import com.ventura.umusic.entity.music.MP3File;
import com.ventura.umusic.music.TracksManager;

//http://en.wikipedia.org/wiki/ID3
@EActivity(R.layout.activity_music_info)
public class MusicInfoActivity extends BaseActivity {
	final String TAG = getClass().getName();

	@ViewById(R.id.music_text_field)
	protected EditText txtMusicTitle;

	@ViewById(R.id.artist_text_field)
	protected EditText txtArtistName;

	@ViewById(R.id.album_text_field)
	protected EditText txtAlbum;

	@ViewById(R.id.comment_text_field)
	protected EditText txtComment;

	@ViewById(R.id.composer_text_field)
	protected EditText txtComposer;

	@ViewById(R.id.genre_text_field)
	protected EditText txtGenre;

	@ViewById(R.id.track_number_text_field)
	protected EditText txtTrackNumber;

	@ViewById(R.id.year_text_field)
	protected EditText txtYear;

	@ViewById(R.id.lyrics_text_field)
	protected EditText txtLyrics;

	private MP3File mCurrentMusicFile;

	private TracksManager mTracksManager = new TracksManager(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@AfterViews
	protected void afterViews() {
		String action = getIntent().getAction();
		if (action != null && !Intent.ACTION_MAIN.equals(action))
			decipherIntent();
	}

	private void decipherIntent() {
		Intent intent = this.getIntent();

		String mimeType = intent.getType();
		Bundle extras = intent.getExtras();

		Uri sharedPath = intent.getData();
		if (sharedPath == null && extras != null)
			sharedPath = (Uri) extras.get(Intent.EXTRA_STREAM);

		File musicFile = Utils.getSharedFile(this, sharedPath);

		if (musicFile == null) {
			Toast.makeText(getApplicationContext(), "File not found.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		Log.i(TAG, mimeType);

		try {
			mCurrentMusicFile = new MP3File(
					new org.farng.mp3.MP3File(musicFile));
			mCurrentMusicFile.getTrack().setPathUri(sharedPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		}

		if (mimeType != null && mimeType.startsWith("audio/")) {
			this.loadMusicTags(mCurrentMusicFile);
		}
	}

	private void loadMusicTags(MP3File track) {
		txtMusicTitle.setText(track.getSongTitle());
		txtArtistName.setText(track.getArtist());
		txtAlbum.setText(track.getAlbumTitle());
		txtComment.setText(track.getSongComment());
		txtComposer.setText(track.getComposer());
		byte genre = track.getSongGenre();
		txtGenre.setText((String) TagConstant.genreIdToString.get(genre));
		txtTrackNumber.setText(track.getTrackPosition());
		txtYear.setText(track.getYearReleased());
		txtLyrics.setText(track.getLyrics());
	}

	private void findLyrics(String artistName, String musicTitle) {
		if (!musicTitle.equals("") && !artistName.equals("")) {
			Intent intent = new Intent(this,
					com.ventura.umusic.ui.music.LyricsViewerActivity_.class);
			intent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME, artistName);
			intent.putExtra(GlobalConstants.EXTRA_TRACK_NAME, musicTitle);
			startActivity(intent);
		}
	}

	private void openArtistInfo(String artistName) {
		// Verifying internet connection...
		if (!ConnectionManager.isConnected(this)) {
			Toast.makeText(this,
					this.getString(R.string.message_no_internet_connection),
					Toast.LENGTH_LONG).show();
		} else if (artistName != null && !artistName.equals("")) {
			Intent intent = new Intent(this,
					com.ventura.umusic.ui.artist.ArtistsListActivity_.class);
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(GlobalConstants.EXTRA_SEARCH_TEXT, artistName);
			startActivity(intent);
		}
	}

	public void saveChanges() {
		if (mCurrentMusicFile != null) {
			try {
				mCurrentMusicFile.setSongTitle(txtMusicTitle.getText()
						.toString());
				mCurrentMusicFile.setArtist(txtArtistName.getText().toString());
				mCurrentMusicFile.setAlbumTitle(txtAlbum.getText().toString());
				mCurrentMusicFile.setSongComment(txtComment.getText()
						.toString());
				mCurrentMusicFile.setComposer(txtComposer.getText().toString());
				byte genre = -1;
				try {
					genre = (Byte) TagConstant.genreStringToId.get(txtGenre
							.getText().toString());
				} catch (Exception e) {
					Log.i(TAG, "Genre invalid: "
							+ txtGenre.getText().toString());
				}
				mCurrentMusicFile.setSongGenre(genre);
				mCurrentMusicFile.setTrackPosition(txtTrackNumber.getText()
						.toString());
				mCurrentMusicFile.setYearReleased(txtYear.getText().toString());
				mCurrentMusicFile.setLyrics(txtLyrics.getText().toString());

				mCurrentMusicFile.save();

				mTracksManager.scanMedia(mCurrentMusicFile.getTrack()
						.getPathUri().toString());

				Toast.makeText(this, "File saved.", Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				e.printStackTrace();
				alert(e.getMessage());
			} catch (TagException e) {
				alert(e.getMessage());
			}
		}
	}

	// LISTENERS

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("Actions");
		sub.add(0, R.id.menu_view_artist_info, 0, R.string.search_artist);
		sub.add(0, R.id.menu_find_lyrics, 0, R.string.search_lyrics);
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_view_artist_info:
			this.openArtistInfo(txtArtistName.getText().toString());
			break;
		case R.id.menu_find_lyrics:
			this.findLyrics(this.txtArtistName.getText().toString(),
					this.txtMusicTitle.getText().toString());
			break;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}

	public void onSaveButtonClicked(View view) {
		saveChanges();
	}

	public void onViewArtistInfoBtnClick(View button) {
		this.openArtistInfo(txtArtistName.getText().toString());
	}
}
