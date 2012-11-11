package com.ventura.lyricsfinder.discogs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.ventura.lyricsfinder.discogs.entities.Image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoaderTask extends AsyncTask<Void, Void, Bitmap> {

	private ImageView mTarget;
	private Image mImage;

	public ImageLoaderTask(ImageView target, Image img) {
		this.mTarget = target;
		this.mImage = img;
	}

	@Override
	protected Bitmap doInBackground(Void... nothing) {
		URL url;
		Bitmap bmp = null;
		try {
			url = new URL(this.mImage.getUri().toString());
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


		if (this.mImage.getHeight() > 0 && this.mImage.getWidth() > 0) {
			this.mTarget.setMinimumHeight(this.mImage.getHeight());
			this.mTarget.setMinimumWidth(this.mImage.getWidth());
		}
		this.mTarget.setImageBitmap(result);
	}

}
