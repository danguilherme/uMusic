package com.ventura.lyricsfinder.discogs;

import java.util.EventListener;

import android.graphics.Bitmap;

public interface OnContentDownloadListener extends EventListener {

	public void onDownloadFinished(Object result);
	
	public void onDownloadError(String error);

}
