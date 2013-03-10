package com.ventura.lyricsfinder.discogs;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.musicexplorer.R;
import com.ventura.lyricsfinder.util.ImageLoader;

public class LazyAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public void add(ArrayList<HashMap<String, String>> data) {
		this.data.addAll(data);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public void filter(String query) {
		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).get(DiscogsConstants.KEY_TITLE)
					.equalsIgnoreCase(query)) {
				data.remove(i);
			}
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public String getId(int position) {
		return data.get(position).get(DiscogsConstants.KEY_ID).toString();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_item, null);

		// artist name
		TextView artist = (TextView) vi.findViewById(R.id.artist);
		// thumb image
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image);

		HashMap<String, String> song = new HashMap<String, String>();
		song = data.get(position);

		// Setting all values in listview
		artist.setText(song.get(DiscogsConstants.KEY_TITLE));
		imageLoader.displayImage(song.get(DiscogsConstants.KEY_THUMB),
				thumb_image);
		return vi;
	}

}
