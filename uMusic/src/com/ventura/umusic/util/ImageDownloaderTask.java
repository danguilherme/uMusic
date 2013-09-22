package com.ventura.umusic.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.ventura.umusic.entity.Image;

public class ImageDownloaderTask extends AsyncTask<ImageView, Void, Bitmap> {

	private boolean isDownloading = false;

	private Image mImage;
	private OnImageDownloadListener imageDownloadListener;

	public ImageDownloaderTask() {
	}

	public ImageDownloaderTask(Image img,
			OnImageDownloadListener imageDownloadListener) {
		this.mImage = img;
		this.imageDownloadListener = imageDownloadListener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.isDownloading = true;
	}

	@Override
	protected Bitmap doInBackground(ImageView... nothing) {
		Bitmap bitmap = null;
		try {
			if (this.mImage != null) {
				bitmap = BitmapFactory.decodeStream(this.mImage.getUrl()
						.openConnection().getInputStream());
			}
		} catch (IOException e) {
			this.imageDownloadListener.onDownloadError(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			this.imageDownloadListener.onDownloadError(e.getMessage());
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		this.isDownloading = false;
		if (result != null) {
			imageDownloadListener.onDownloadFinished(result);
		}
	}

	public boolean isDownloading() {
		return this.isDownloading;
	}

	public void setImageDownloadListener(
			OnImageDownloadListener imageDownloadListener) {
		this.imageDownloadListener = imageDownloadListener;
	}

	public Image getImage() {
		return mImage;
	}

	public void setImage(Image image) {
		this.mImage = image;
	}
}
