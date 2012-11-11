package com.ventura.lyricsfinder.activities;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.discogs.DiscogsConstants;
import com.ventura.lyricsfinder.discogs.entities.QueryType;

public class MusicInfoActivity extends BaseActivity {
	private EditText mTitleTextField;
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

		Intent intent = this.getIntent();
		this.loadMusicTags(intent);

		Button button = (Button) this.findViewById(R.id.btn_open_main_activity);
		Button btnViewArtistInfo = (Button) this
				.findViewById(R.id.btn_view_artist_info);

		/*
		 * button.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View view) { Intent intent = new
		 * Intent(view.getContext(), MainActivity.class);
		 * intent.setAction(Intent.ACTION_SEND); if (mActualLyricPath != null) {
		 * Bundle parameters = new Bundle();
		 * parameters.putString(Intent.EXTRA_STREAM,
		 * mActualLyricPath.getPath()); intent.setType("audio/mpeg");
		 * intent.putExtras(parameters); } startActivity(intent); } });
		 */

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
			}
		}
	}

	private void defineVariables() {
		this.mTitleTextField = (EditText) this
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

			mTitleTextField.setText(songTitle);
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

	private String getFilePathFromContentUri(Uri fileUri,
			ContentResolver contentResolver) {
		// See http://stackoverflow.com/a/11603837
		String filePath;
		String[] filePathColumn = { MediaStore.MediaColumns.DATA };

		Cursor cursor = contentResolver.query(fileUri, filePathColumn, null,
				null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
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
