package com.ventura.umusic.ui.release;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.androidutils.ui.BaseAdapter;
import com.ventura.umusic.R;
import com.ventura.umusic.discogs.entity.ArtistRelease;

public class ReleasesListAdapter extends BaseAdapter<ArtistRelease> {
	private final String TAG = getClass().getName();
	
	public ReleasesListAdapter(Context context, List<ArtistRelease> data) {
		super(context, data);
	}

	private final int LAYOUT_ID = R.layout.list_item_all_detail;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(LAYOUT_ID, null);
			convertView.setTag(viewHolder);

			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.thumbnail);
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.main_text);

			viewHolder.year = (TextView) convertView
					.findViewById(R.id.thumbnail_legend);
			viewHolder.format = (TextView) convertView
					.findViewById(R.id.secondary_text);
			viewHolder.trackInfo = (TextView) convertView
					.findViewById(R.id.extra_text);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ArtistRelease release = (ArtistRelease) this.getItem(position);

		// Setting all values in listview
		viewHolder.title.setText(release.getTitle());
		if (release.getThumbImage() != null
				&& release.getThumbImage().getUrl() != null) {
			imageLoader.displayImage(release.getThumbImage().getUrl()
					.toString(), viewHolder.image);
		}
		viewHolder.year.setText(String.valueOf(release.getYear()));
		viewHolder.trackInfo.setText(release.getTrackInfo());
		viewHolder.format.setText(release.getFormat());
		return convertView;
	}

	@Override
	public String getId(int position) {
		return String.valueOf(this.data.get(position).getId());
	}

	static class ViewHolder {
		ImageView image;
		TextView title;
		TextView year;
		TextView trackInfo;
		TextView format;
	}

}
