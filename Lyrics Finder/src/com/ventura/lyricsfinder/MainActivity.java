package com.ventura.lyricsfinder;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ventura.lyricsfinder.lyrics.Lyric;
import com.ventura.lyricsfinder.lyrics.provider.LyricProvider;
import com.ventura.lyricsfinder.lyrics.provider.LyricProviders;

public class MainActivity extends Activity {
	private ProvidersDbAdapter mProvidersDbHelper;
	private Lyric mActualLyric = new Lyric();
	private LyricProvider mActualProvider;
	private LyricDownloadTask mLyricDownloadTask;

	private EditText mMusicTextField;
	private EditText mArtistTextField;
	private TextView mLyricTextView;
	private Button mFindButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mMusicTextField = (EditText) findViewById(R.id.music_text_field);
		this.mArtistTextField = (EditText) findViewById(R.id.artist_text_field);
		this.mLyricTextView = (TextView) findViewById(R.id.lyrics_text_view);
		this.mFindButton = (Button) findViewById(R.id.button_find_lyrics);
		
		this.mProvidersDbHelper = new ProvidersDbAdapter(this);
		this.mProvidersDbHelper.open();

		// this.loadProviders();

		try {
			mActualProvider = mProvidersDbHelper
					.fetchProvider(LyricProviders.AzLyrics);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		Uri returnedUri = intent.getData();

		if (Intent.ACTION_VIEW.equals(action) && type != null) {
			if (returnedUri == null) {
				Toast.makeText(getApplicationContext(), "Uri is Null",
						Toast.LENGTH_LONG).show();
			} else if (type.startsWith("audio/")) {
				this.loadMusicTags(returnedUri.getPath());
				// this.getLyrics();
				// this.mFindButton.performClick();
			}
		} else if (Intent.ACTION_SEND.equals(action) && type != null) {
			Uri sharedUri = null;
			if (returnedUri != null) {
				sharedUri = returnedUri;
			} else {
				sharedUri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
			}

			this.loadMusicTags(sharedUri.getPath());
		}

		if (!Intent.ACTION_MAIN.equals(action)) {
			mLyricTextView.setText("Retrieving your music.\n"
					+ "Don't click the \"" + getString(R.string.btn_find_lyric)
					+ "\" button.\n"
					+ "Loading dialog still under development... :)");
		}
		this.mFindButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getLyrics();
			}
		});
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

	private void loadMusicTags(String musicPath) {
		File src = new File(musicPath);
		MusicMetadataSet musicMetadataSet = null;
		try {
			musicMetadataSet = new MyID3().read(src);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (musicMetadataSet == null) // perhaps no metadata
		{
			Toast.makeText(getApplicationContext(),
					R.string.message_no_music_metadata_found, Toast.LENGTH_LONG)
					.show();
		} else {
			String artist = null;
			String songTitle = null;
			try {
				IMusicMetadata metadata = musicMetadataSet.getSimplified();
				artist = metadata.getArtist();
				// String album = metadata.getAlbum();
				songTitle = metadata.getSongTitle();
				// Number track_number = metadata.getTrackNumber();
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.mActualLyric.setArtistName(artist);
			this.mActualLyric.setMusicName(songTitle);
			mArtistTextField.setText(artist);
			mMusicTextField.setText(songTitle);

			// this.getLyrics();

			mLyricDownloadTask = new LyricDownloadTask(mActualProvider,
					this.mLyricTextView, null);
			mLyricDownloadTask.execute(this.mActualLyric);
		}
	}

	// private void setMusicTags() {
	// File dst = new File(path_to_file);
	// MusicMetadata meta = new MusicMetadata("name");
	// meta.setAlbum("Chirag");
	// meta.setArtist("CS");
	//
	// try {
	// new MyID3().write(src, dst, src_set, meta);
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ID3WriteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } // write updated metadata
	// }

	public void loadProviders() {
		mProvidersDbHelper.createProvider(
				getString(R.string.provider_terra_name),
				getString(R.string.provider_terra_url),
				LyricProviders.TerraLetras);

		mProvidersDbHelper.createProvider(
				getString(R.string.provider_lyrster_name),
				getString(R.string.provider_lyrster_url),
				LyricProviders.Lyrster);

		mProvidersDbHelper.createProvider(
				getString(R.string.provider_azlyrics_name),
				getString(R.string.provider_azlyrics_url),
				LyricProviders.AzLyrics);
	}

	private void getLyrics() {
		String musicName = this.mMusicTextField.getText().toString();
		String artistName = this.mArtistTextField.getText().toString();

		Lyric lyric = new Lyric();
		if (!this.isConected(this)) {
			Toast.makeText(getApplicationContext(),
					R.string.message_no_internet_connection, Toast.LENGTH_SHORT)
					.show();
		} else if (!musicName.equals("") && !artistName.equals("")) {
			lyric.setArtistName(artistName);
			lyric.setMusicName(musicName);

			ProgressDialog progDialog = ProgressDialog.show(this,
					getString(R.string.message_fetching_lyric_title),
					getString(R.string.message_fetching_lyric_body) + " - "
							+ mActualProvider.getName(), true);
			// progDialog.setCancelable(true);

			mLyricDownloadTask = new LyricDownloadTask(mActualProvider,
					this.mLyricTextView, progDialog);
			mLyricDownloadTask.execute(lyric);
		}
	}

	@Override
	public void onBackPressed() {
		// if (!mLyricDownloadTask.isCancelled()) {
		// mLyricDownloadTask.cancel(true);
		// }
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		mProvidersDbHelper.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.menu_see_providers:
				System.out.println("item click");
				return true;

			case R.id.menu_terra:
				mActualProvider = mProvidersDbHelper
						.fetchProvider(LyricProviders.TerraLetras);
				return true;
			case R.id.menu_lyrster:
				mActualProvider = mProvidersDbHelper
						.fetchProvider(LyricProviders.Lyrster);
				return true;
			case R.id.menu_azlyrics:
				mActualProvider = mProvidersDbHelper
						.fetchProvider(LyricProviders.AzLyrics);
				return true;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public boolean isConected(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isConnected()) { // Status de conexão 3G
				System.out.println("Status de conexão 3G: "
						+ cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
								.isConnected());
				return true;
			} else if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnected()) { // Status de conexão 3G
				System.out.println("Status de conexão Wifi: "
						+ cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
								.isConnected());
				return true;
			} else { // Não possui conexão com a internet
				System.out.println("Status de conexão Wifi: "
						+ cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
								.isConnected());
				System.out.println("Status de conexão 3G: "
						+ cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
								.isConnected());
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
