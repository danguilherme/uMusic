package com.ventura.umusic.ui.release;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ventura.umusic.R;
import com.ventura.umusic.discogs.entity.ArtistRelease;
import com.ventura.umusic.ui.BaseAdapter;

public class ReleasesListAdapter extends BaseAdapter<ArtistRelease> {
	public ReleasesListAdapter(Context context, List<ArtistRelease> data) {
		super(context, data);
	}

	private int layoutId = R.layout.artist_releases_list_item;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(layoutId, null);
			convertView.setTag(viewHolder);

			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.release_thumb);
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.release_title);
			

			viewHolder.year = (TextView) convertView
					.findViewById(R.id.release_year);
			viewHolder.trackInfo = (TextView) convertView
					.findViewById(R.id.release_trackinfo);
			viewHolder.format = (TextView) convertView
					.findViewById(R.id.release_status);
//			Button openTracksButton = (Button) convertView
//					.findViewById(R.id.btn_artist_release_open_info);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ArtistRelease release = (ArtistRelease) this.getItem(position);

		// Setting all values in listview
		viewHolder.title.setText(release.getTitle());
		if (release.getThumbImage() != null && release.getThumbImage().getUrl() != null) {
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
