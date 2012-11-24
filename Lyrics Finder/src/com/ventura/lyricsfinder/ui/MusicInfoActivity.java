package com.ventura.lyricsfinder.ui;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ventura.lyricsfinder.GlobalConstants;
import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.entities.QueryType;
import com.ventura.lyricsfinder.discogs.ui.ListArtistsActivity;
import com.ventura.lyricsfinder.musixmatch.ui.Constants;
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
	private Uri mActualLyricPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.music_info);

		this.defineVariables();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = this.getIntent();
		this.loadMusicTags(intent);

		Button button = (Button) this.findViewById(R.id.btn_open_main_activity);
		Button btnViewArtistInfo = (Button) this
				.findViewById(R.id.btn_view_artist_info);

		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String artist = mArtistTextField.getText().toString();
				String song = mMusicTitleTextField.getText().toString();
				if (!song.equals("") && !artist.equals("")) {
					Intent intent = new Intent(view.getContext(),
							LyricsViewerActivity.class);
					intent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME, artist);
					intent.putExtra(GlobalConstants.EXTRA_TRACK_NAME, song);
					startActivity(intent);
				}
			}
		});

		btnViewArtistInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String artist = mArtistTextField.getText().toString();
				if (artist != null && !artist.equals("")) {
					Intent intent = new Intent(view.getContext(),
							ListArtistsActivity.class);
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(DiscogsConstants.KEY_QUERY_TYPE,
							QueryType.Artist.toString());
					intent.putExtra(DiscogsConstants.KEY_QUERY_TEXT, artist);
					startActivity(intent);
				}
			}
		});
	}

	private void loadMusicTags(Intent intent) {
		String action = intent.getAction();
		String type = intent.getType();
		Uri sharedPath = intent.getData();
		if (sharedPath == null && intent.getExtras() != null) {
			sharedPath = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
		}
		mActualLyricPath = sharedPath;
		File song = getMusicFile(mActualLyricPath);

		if (action != null && !action.equals(Intent.ACTION_MAIN)) {
			MusicMetadataSet musicMetadataSet = null;
			if (song == null) {
				Toast.makeText(getApplicationContext(), "No file found.",
						Toast.LENGTH_LONG).show();
				action = null;
			} else {
				try {
					musicMetadataSet = new MyID3().read(song);
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
					file = new MP3File(song);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TagException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (file.hasLyrics3Tag()) {
					Log.i(TAG, "hasLyrics3Tag");
					Log.i(TAG, file.getLyrics3Tag().getSongLyric());
				} else {
					Log.i(TAG, "doesntHaveLyrics3Tag");
				}

				if (file.hasID3v1Tag()) {
					Log.i(TAG, "hasID3v1Tag");
					Log.i(TAG, "Artist: " + file.getID3v1Tag().getArtist());
					Log.i(TAG, "Title: " + file.getID3v1Tag().getSongTitle());
				} else {
					Log.i(TAG, "doesntHaveID3v1Tag");
				}

				if (file.hasID3v2Tag()) {
					Log.i(TAG, "hasID3v2Tag");
					Log.i(TAG, "Lyric Before: "
							+ file.getID3v2Tag().getSongLyric().length());
					MusixMatch mm = new MusixMatch(Constants.API_KEY);
					try {
						int trackID = mm
								.getMatchingTrack(
										file.getID3v2Tag().getSongTitle(),
										file.getID3v2Tag().getLeadArtist())
								.getTrack().getTrackId();
						file.getID3v2Tag().setSongLyric(
								mm.getLyrics(trackID).getLyricsBody());
					} catch (MusixMatchException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					try {
						file.save(TagConstant.MP3_FILE_SAVE_OVERWRITE);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TagException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String filePath = Environment
							.getExternalStoragePublicDirectory(
									Environment.DIRECTORY_PICTURES)
							.getAbsolutePath()
							+ "/" + song.getName();
					MediaScannerConnection.scanFile(this,
							new String[] { filePath }, null, null);
					Log.i(TAG, "Lyric After: "
							+ file.getID3v2Tag().getSongLyric().length());
					Log.i(TAG, "Title: " + file.getID3v2Tag().getSongTitle());
					AbstractID3v2 t = file.getID3v2Tag();
				} else {
					Log.i(TAG, "doesntHaveID3v2Tag");
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

	private File getMusicFile(Uri path) {
		if (path == null)
			return null;

		String uri;
		if (path.toString().startsWith("content")) {
			uri = this.getFilePathFromContentUri(path, getContentResolver());
		} else {
			uri = path.getPath();
		}

		return new File(uri);
	}
}
