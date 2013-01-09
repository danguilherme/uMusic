package com.ventura.lyricsfinder.ui;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2Frame;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.entity.enumerator.QueryType;
import com.ventura.lyricsfinder.discogs.ui.ListArtistsActivity;
import com.ventura.lyricsfinder.musixmatch.ui.LyricsViewerActivity;

public class MusicInfoActivity extends BaseActivity {
	final String TAG = getClass().getName();

	private EditText mMusicTitleTextField;
	private EditText mArtistTextField;
	private EditText mAlbumTextField;
	private EditText mCommentTextField;
	private EditText mCompilationTextField;
	private EditText mComposerTextField;
	private EditText mComposer2TextField;
	private EditText mDurationTextField;
	private EditText mFeaturingListTextField;
	private EditText mGenreTextField;
	private EditText mProducerTextField;
	private EditText mProducerArtistTextField;
	private EditText mTrackNumberTextField;
	private EditText mYearTextField;

	private File mCurrentMusicFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.music_info);

		this.defineVariables();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = this.getIntent();
		this.loadMusicTags(intent);
	}

	public void onSaveButtonClicked(View view) {
		if (mCurrentMusicFile != null) {
			try {
				this.saveFile(new MP3File(mCurrentMusicFile));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TagException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadMusicTags(Intent intent) {
		String action = intent.getAction();
		String type = intent.getType();
		Uri sharedPath = intent.getData();
		if (sharedPath == null && intent.getExtras() != null) {
			sharedPath = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
		}

		mCurrentMusicFile = getSharedFile(sharedPath);

		if (action != null && !action.equals(Intent.ACTION_MAIN)) {
			MusicMetadataSet musicMetadataSet = null;
			if (mCurrentMusicFile == null) {
				Toast.makeText(getApplicationContext(), "No file found.",
						Toast.LENGTH_LONG).show();
				action = null;
			} else {
				try {
					musicMetadataSet = new MyID3().read(mCurrentMusicFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			if (Intent.ACTION_VIEW.equals(action) && type != null) {
				if (type.startsWith("audio/")) {
					this.bindData(musicMetadataSet);
				}
			} else if (Intent.ACTION_SEND.equals(action) && type != null) {
				this.bindData(musicMetadataSet);

				org.farng.mp3.MP3File file = null;
				try {
					file = new MP3File(mCurrentMusicFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TagException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (file.hasLyrics3Tag()) {
					Log.i(TAG, "File has Lyrics3 tags ------------------");
					Log.i(TAG, "Lyrics: " + file.getLyrics3Tag().getSongLyric());
				} else {
					Log.i(TAG, "File does not have Lyrics3 tags --------");
				}

				if (file.hasID3v1Tag()) {
					Log.i(TAG, "File has ID3v1 tags --------------------");
					String artist = file.getID3v1Tag().getArtist(), music = file
							.getID3v1Tag().getSongTitle();

					Log.i(TAG, "Artist: " + artist);
					Log.i(TAG, "Title: " + music);
				} else {
					Log.i(TAG, "File does not have ID3v1 tags ----------");
				}

				if (file.hasID3v2Tag()) {

					AbstractID3v2Frame lyricsTagFrame = file.getID3v2Tag()
							.getFrame(
									"USLT" + ((char) 0) + "XXX" + ((char) 0)
											+ "");
					String lyrics = null;
					if (lyricsTagFrame != null) {
						lyrics = lyricsTagFrame.getBody()
								.getObject("Lyrics/Text").toString();
					}

					// If exists lyrics in the old tag frame, copy it to the new
					// lyrics tag.
					/*
					 * if (lyrics != null && !lyrics.equals("")) {
					 * file.getID3v2Tag().setSongLyric(lyrics); saveFile(file);
					 * }
					 */

					Log.i(TAG, "File has ID3v2 tags --------------------");
					// AbstractID3v2 t = file.getID3v2Tag();
					String artist = file.getID3v2Tag().getLeadArtist(), music = file
							.getID3v2Tag().getSongTitle();
					lyrics = file.getID3v2Tag().getSongLyric();

					Log.i(TAG, "Artist: " + artist);
					Log.i(TAG, "Title: " + music);
					Log.i(TAG, "Lyrics: "
							+ (lyrics != null ? lyrics.length() : null)
							+ " characters");
				} else {
					Log.i(TAG, "File does not have ID3v2 tags ----------");
				}
			}
		}
	}

	private void defineVariables() {
		this.mMusicTitleTextField = (EditText) this
				.findViewById(R.id.music_text_field);
		this.mArtistTextField = (EditText) this
				.findViewById(R.id.artist_text_field);
		this.mAlbumTextField = (EditText) this
				.findViewById(R.id.album_text_field);
		this.mCommentTextField = (EditText) this
				.findViewById(R.id.comment_text_field);
		this.mCompilationTextField = (EditText) this
				.findViewById(R.id.compilation_text_field);
		this.mComposerTextField = (EditText) this
				.findViewById(R.id.composer_text_field);
		this.mComposer2TextField = (EditText) this
				.findViewById(R.id.composer2_text_field);
		this.mDurationTextField = (EditText) this
				.findViewById(R.id.duration_text_field);
		this.mFeaturingListTextField = (EditText) this
				.findViewById(R.id.featuring_list_text_field);
		this.mGenreTextField = (EditText) this
				.findViewById(R.id.genre_text_field);
		this.mProducerTextField = (EditText) this
				.findViewById(R.id.producer_text_field);
		this.mProducerArtistTextField = (EditText) this
				.findViewById(R.id.producer_artist_text_field);
		this.mTrackNumberTextField = (EditText) this
				.findViewById(R.id.track_number_text_field);
		this.mYearTextField = (EditText) this
				.findViewById(R.id.year_text_field);
	}

	private void bindData(MusicMetadataSet musicMetadataSet) {
		if (musicMetadataSet == null) // perhaps no metadata
		{
			Toast.makeText(getApplicationContext(),
					R.string.message_no_music_metadata_found, Toast.LENGTH_LONG)
					.show();
		} else {

			String songTitle = null;
			String artist = null;
			String album = null;
			String comment = null;
			String compilation = null;
			String composer = null;
			String composer2 = null;
			String duration = null;
			String featuringList = null;
			String genre = null;
			String producer = null;
			String producerArtist = null;
			String trackNumber = null;
			String year = null;
			try {
				IMusicMetadata metadata = musicMetadataSet.getSimplified();
				songTitle = metadata.getSongTitle();
				artist = metadata.getArtist();
				album = metadata.getAlbum();
				comment = metadata.getComment();
				compilation = metadata.getCompilation();
				composer = metadata.getComposer();
				composer2 = metadata.getComposer2();
				duration = metadata.getDurationSeconds();
				// featuringList = metadata.getFeaturingList();
				genre = metadata.getGenre();
				producer = metadata.getProducer();
				producerArtist = metadata.getProducerArtist();
				trackNumber = metadata.getTrackNumber().toString();
				year = metadata.getYear();
			} catch (Exception e) {
				e.printStackTrace();
			}

			mMusicTitleTextField.setText(songTitle);
			mArtistTextField.setText(artist);
			mAlbumTextField.setText(album);
			mCommentTextField.setText(comment);
			mCompilationTextField.setText(compilation);
			mComposerTextField.setText(composer);
			mComposer2TextField.setText(composer2);
			mDurationTextField.setText(duration);
			mFeaturingListTextField.setText(featuringList);
			mGenreTextField.setText(genre);
			mProducerTextField.setText(producer);
			mProducerArtistTextField.setText(producerArtist);
			mTrackNumberTextField.setText(trackNumber);
			mYearTextField.setText(year);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = new MenuInflater(this);
		menuInflater.inflate(R.menu.music_info_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_find_lyrics:
			this.findLyrics(this.mArtistTextField.getText().toString(),
					this.mMusicTitleTextField.getText().toString());
			break;
		case R.id.menu_view_artist_info:
			this.viewArtistInfo(mArtistTextField.getText().toString());
			break;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}

	private void findLyrics(String artistName, String musicTitle) {
		if (!musicTitle.equals("") && !artistName.equals("")) {
			Intent intent = new Intent(this, LyricsViewerActivity.class);
			intent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME, artistName);
			intent.putExtra(GlobalConstants.EXTRA_TRACK_NAME, musicTitle);
			startActivity(intent);
		}
	}

	private void viewArtistInfo(String artistName) {
		if (artistName != null && !artistName.equals("")) {
			Intent intent = new Intent(this, ListArtistsActivity.class);
			intent.setAction(Intent.ACTION_SEND);
			intent.putExtra(DiscogsConstants.KEY_QUERY_TYPE,
					QueryType.Artist.toString());
			intent.putExtra(DiscogsConstants.KEY_QUERY_TEXT, artistName);
			startActivity(intent);
		}
	}

	public void saveFile(MP3File file) {
		try {
			MP3File mp3File = new MP3File(file);
			mp3File.save(TagConstant.MP3_FILE_SAVE_OVERWRITE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TagException e) {
			e.printStackTrace();
		}
	}
}
