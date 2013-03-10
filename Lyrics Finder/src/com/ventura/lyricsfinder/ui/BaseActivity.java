package com.ventura.lyricsfinder.ui;

import java.io.File;
import java.util.List;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ventura.musicexplorer.R;
import com.ventura.lyricsfinder.discogs.oauth.Constants;

public abstract class BaseActivity extends SherlockActivity {
	final String TAG = getClass().getName();
	protected SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		Intent intent = this.getIntent();
		String action = intent.getAction();

		if (action == null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			if (!action.equals(Intent.ACTION_MAIN)) {
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
	}

	protected void clearCredentials() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}

	public OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
				Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

	public String getFilePathFromContentUri(Uri fileUri,
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

	public File getSharedFile(Uri path) {
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

	/**
	 * This method creates a list of strings, one bellow another, separated by a
	 * semicomma and finalized with a dot. The output is like this:
	 * <ul>
	 * <li>First item;</li>
	 * <li>Item 2;</li>
	 * <li>Item 3;</li>
	 * <li>Last item.</li>
	 * </ul>
	 * 
	 * <b>Note:</b> Make sure the <code>toString()</code> method of the
	 * <code>T</code> class will return what you expect to show up at the list.
	 * 
	 * @param source
	 *            From where the strings will be catch from.
	 * @return The strings list, with line breaks as \r\n chars.
	 */
	protected <T extends Object> String createList(List<T> source) {
		StringBuilder list = new StringBuilder();

		if (source == null) {
			return null;
		}

		for (int i = 0; i < source.size(); i++) {
			Object currentString = source.get(i);
			list.append(currentString.toString());

			// If it's the last item
			if ((i + 1) == source.size()) {
				// Add a final dot.
				list.append(".");
			} else {
				// Else, add a comma after the name
				list.append(";\r\n");
			}
		}
		return list.toString();
	}

	/**
	 * Shows an alert dialog on the device screen.
	 * 
	 * @param messageResourceId
	 *            The resource id of the message string
	 */
	public void alert(int messageResourceId) {
		this.alert(this.getResources().getString(messageResourceId));
	}

	/**
	 * Shows an alert dialog on the device screen. The title of this alert is
	 * the app's name
	 * 
	 * @param message
	 *            The message to show
	 */
	public void alert(String message) {
		this.alert(this.getResources().getString(R.string.app_name), message);
	}

	/**
	 * Shows an alert dialog on the device screen.
	 * 
	 * @param title
	 *            The title of the alert window
	 * @param message
	 *            The message to show
	 */
	public void alert(String title, String message) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
	}
}
