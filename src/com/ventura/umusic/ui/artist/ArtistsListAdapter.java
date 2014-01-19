package com.ventura.umusic.ui.artist;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.androidutils.ui.BaseAdapter;
import com.ventura.umusic.R;
import com.ventura.umusic.entity.artist.Artist;

public class ArtistsListAdapter extends BaseAdapter<Artist> {
	final String TAG = getClass().getName();
	public ArtistsListAdapter(Context context, List<Artist> data) {
		super(context, data);
	}

	private int layoutId = R.layout.list_item_default;

	public void filter(String query) {
		for (int i = 0; i < this.data.size(); i++) {
			if (!this.data.get(i).getName().equalsIgnoreCase(query)) {
				this.data.remove(i);
			}
		}
	}

	public String getId(int position) {
		return String.valueOf(this.data.get(position).getId());
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		try {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = inflater.inflate(layoutId, null);
				convertView.setTag(viewHolder);

				viewHolder.artistImage = (ImageView) convertView
						.findViewById(R.id.thumbnail);
				viewHolder.artistName = (TextView) convertView
						.findViewById(R.id.main_text);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Artist artist = (Artist) this.getItem(position);

			// Setting all values in listview
			viewHolder.artistName.setText(artist.getName());
			if (artist.getImages() != null && artist.getImages().size() > 0) {
				imageLoader.displayImage(artist.getImages().get(0).getUrl()
						.toString(), viewHolder.artistImage);
			}

		} catch (Exception e) {
			Log.e(TAG, "Error on getting list item", e);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView artistImage;
		TextView artistName;
	}
}
