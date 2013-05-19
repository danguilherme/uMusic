package com.ventura.musicexplorer.ui;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ventura.androidutils.utils.ConnectionManager;
import com.ventura.musicexplorer.R;

public class BaseListActivity extends SherlockListActivity {
	final String TAG = getClass().getName();
	protected SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
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

	public void showNoInternetMessage() {
		Toast.makeText(this, R.string.message_no_internet_connection,
				Toast.LENGTH_LONG).show();
	}

	public void showLazyInternetMessage() {
		Toast.makeText(this, R.string.message_lazy_internet_connection,
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Verify if the activity is connected to the internet
	 * 
	 * @return The connection status. <code>true</code> if connected and
	 *         <code>false</code> otherwise.
	 */
	public boolean isConnected() {
		return ConnectionManager.isConnected(this);
	}
}
