package com.ventura.umusic.entity;

import java.io.Serializable;
import java.net.URL;

public class Image implements Serializable {
	private static final long serialVersionUID = -6428170031436031037L;

	private URL url;
	private int height = 0;
	private int width = 0;

	//private Bitmap bitmap;

	public Image(URL uri, int width, int height/*, Bitmap bitmap*/) {
		this.setUrl(uri);
		this.width = width;
		this.height = height;
		//this.bitmap = bitmap;
	}

	public Image(URL uri, int width, int height, String type) {
		this.setUrl(uri);
		this.width = width;
		this.height = height;
	}

	public Image(URL thumbUri) {
		this.setUrl(thumbUri);
	}

	public Image() {
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	/*public Bitmap getBitmap() {
		return this.bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}*/

	@Override
	public String toString() {
		return "Image (" + this.url + ", " + this.width + "x" + this.height
				+ ")";
	}

}
