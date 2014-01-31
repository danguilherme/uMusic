package com.ventura.umusic.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.ventura.androidutils.ui.widget.KeyValuePanel;
import com.ventura.umusic.BaseApplication;
import com.ventura.umusic.R;

@EActivity(R.layout.about)
public class AboutActivity extends BaseActivity {

	KeyValuePanel developerName;

	KeyValuePanel appVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@AfterViews
	void updateText() {
		developerName = (KeyValuePanel) findViewById(R.id.developer_name);
		appVersion = (KeyValuePanel) findViewById(R.id.app_version);

		appVersion.setValue(BaseApplication.APP_VERSION);
	}

	@Click(R.id.btn_rate_app)
	void openGooglePlayPage() {
		try {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("market://details?id=" + getPackageName()));
			startActivity(i);
		} catch (Exception e) {
			Log.e(TAG, "Error opening Google Play", e);
			alert("Error opening Google Play");
		}
	}

	@Click(R.id.btn_send_email)
	void sendEmail() {
		// %20 == space
		String emailUrl = "mailto:%1$s?subject=[%2$s] %3$s";

		emailUrl = String
				.format(emailUrl, getString(R.string.developer_email),
						getString(R.string.app_name),
						getString(R.string.email_subject));

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(emailUrl));
		startActivity(i);
	}
}
