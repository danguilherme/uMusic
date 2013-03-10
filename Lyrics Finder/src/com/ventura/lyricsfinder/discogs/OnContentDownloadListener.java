package com.ventura.lyricsfinder.discogs;

import java.util.EventListener;

public interface OnContentDownloadListener extends EventListener {

	public void onDownloadFinished(Object result);

	public void onDownloadError(String error);

}
