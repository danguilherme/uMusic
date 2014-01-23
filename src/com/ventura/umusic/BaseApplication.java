package com.ventura.umusic;

import com.squareup.picasso.Picasso;

import android.app.Application;
import android.widget.ImageView;

public class BaseApplication extends Application {
	public static String APP_VERSION = "0.1";
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public class Preferences {
		// Music player
		public static final String MP_IS_SHUFFLE = "MP_IS_SHUFFLE";
		public static final String MP_IS_REPEAT = "MP_IS_REPEAT";
		public static final String MP_AUTOLOAD_LYRICS = "MP_AUTOLOAD_LYRICS";
	}
}
