package com.ventura.lyricsfinder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.oauth.PrepareRequestTokenActivity;

public class LoginActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		
		Button launchOauth = (Button) findViewById(R.id.btn_request_permission);
		TextView clearCredentials = (TextView) findViewById(R.id.status);
		 
		launchOauth.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	  startActivity(new Intent().setClass(v.getContext(), PrepareRequestTokenActivity.class));
		    }
		});
	}
}
