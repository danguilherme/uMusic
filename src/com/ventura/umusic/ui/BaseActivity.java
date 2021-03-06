package com.ventura.umusic.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ventura.androidutils.utils.ConnectionManager;
import com.ventura.umusic.BaseApplication;
import com.ventura.umusic.R;

public abstract class BaseActivity extends SherlockActivity {
	final String TAG = getClass().getName();
	protected SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
	}
	
	public BaseApplication getBaseApplication() {
		return (BaseApplication) this.getApplication();
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
	 * @return The strings list, with line breaks artistService \r\n chars.
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
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(title);
		dialog.setMessage(message);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				dialog.show();
			}
		});
	}
	
	public void showToast(final String text, final int duration){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getBaseContext(), text, duration).show();
			}
		});
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
