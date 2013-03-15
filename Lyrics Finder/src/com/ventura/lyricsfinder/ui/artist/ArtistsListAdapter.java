package com.ventura.lyricsfinder.ui.artist;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.lyricsfinder.entity.artist.Artist;
import com.ventura.lyricsfinder.util.ImageLoader;
import com.ventura.musicexplorer.R;

public class ArtistsListAdapter extends BaseAdapter {
	private Activity context;
	private List<Artist> artists;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	private int layoutId = R.layout.artist_list_item;

	public ArtistsListAdapter(Activity activity, List<Artist> artists) {
		this.context = activity;
		this.artists = artists;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public void add(List<Artist> artists) {
		this.artists.addAll(artists);
		this.notifyDataSetChanged();
	}

	public int getCount() {
		return this.artists.size();
	}

	public Object getItem(int position) {
		return this.artists.get(position);
	}

	public void filter(String query) {
		for (int i = 0; i < this.artists.size(); i++) {
			if (!this.artists.get(i).getName().equalsIgnoreCase(query)) {
				this.artists.remove(i);
			}
		}
	}

	public long getItemId(int position) {
		return position;
	}

	public String getId(int position) {
		return String.valueOf(this.artists.get(position).getId());
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(layoutId, null);
			convertView.setTag(viewHolder);

			viewHolder.artistImage = (ImageView) convertView
					.findViewById(R.id.artist_image);
			viewHolder.artistName = (TextView) convertView
					.findViewById(R.id.artist_name);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Artist artist = this.artists.get(position);

		// Setting all values in listview
		viewHolder.artistName.setText(artist.getName());
		if (artist.getImages() != null && artist.getImages().size() > 0) {
			imageLoader.displayImage(artist.getImages().get(0).getUrl()
					.toString(), viewHolder.artistImage);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView artistImage;
		TextView artistName;
	}
}
