package com.ventura.lyricsfinder.util;

import java.util.EventListener;

import android.graphics.Bitmap;

public interface OnImageDownloadListener extends EventListener {

	public void onDownloadFinished(Bitmap result);

	public void onDownloadError(String error);

}
