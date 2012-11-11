package com.ventura.lyricsfinder.discogs;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoaderTask extends AsyncTask<URL, Void, Bitmap> {

	private ImageView mTarget;

	public ImageLoaderTask(ImageView target) {
		this.mTarget = target; 
	}
	
	@Override
	protected Bitmap doInBackground(URL... urls) {
		URL url = urls[0];
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream(url.openConnection()
					.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);

		this.mTarget.setImageBitmap(result);
	}

}
