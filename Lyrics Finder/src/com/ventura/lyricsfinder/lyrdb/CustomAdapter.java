package com.ventura.lyricsfinder.lyrdb;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ventura.lyricsfinder.R;

public class CustomAdapter extends BaseAdapter {
	public static final String MUSIC_NAME = "MUSIC_NAME";
	public static final String ARTIST_NAME = "ARTIST_NAME";

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;

	public CustomAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.list_item_lyric, null);

		TextView title = (TextView) vi.findViewById(R.id.title);
		TextView artist = (TextView) vi.findViewById(R.id.artist);
		HashMap<String, String> lyric = new HashMap<String, String>();
		lyric = data.get(position);

		// Setting all values in listview
		title.setText(lyric.get(CustomAdapter.MUSIC_NAME));
		artist.setText(lyric.get(CustomAdapter.ARTIST_NAME));

		return vi;
	}

}
