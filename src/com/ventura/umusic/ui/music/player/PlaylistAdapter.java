package com.ventura.umusic.ui.music.player;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ventura.androidutils.ui.BaseAdapter;
import com.ventura.umusic.R;
import com.ventura.umusic.entity.music.Audio;
import com.ventura.umusic.music.TracksManager;

public class PlaylistAdapter extends BaseAdapter<String> {

	HashMap<String, Audio> cache;
	TracksManager tm;

	public PlaylistAdapter(Context context, List<String> data) {
		super(context, data);
		tm = new TracksManager(context);
		cache = new HashMap<String, Audio>(data.size());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_multiline, null);
			viewHolder.albumImage = (ImageView) convertView
					.findViewById(R.id.thumbnail);
			viewHolder.songTitle = (TextView) convertView
					.findViewById(R.id.main_text);
			viewHolder.artistName = (TextView) convertView
					.findViewById(R.id.secondary_text);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		String path = get(position);
		Audio song = cache.get(path);
		if (song == null) {
			song = tm.getTrackByUri(path);
			cache.put(path, song);
		}

		Picasso.with(getContext()).load(song.getAlbumArtUri())
				.placeholder(R.drawable.no_image).into(viewHolder.albumImage);

		viewHolder.songTitle.setText(song.getTitle());
		viewHolder.artistName.setText(song.getArtistName());

		return convertView;
	}

	@Override
	public String getId(int position) {
		return data.get(position);
	}

	private static class ViewHolder {
		ImageView albumImage;
		TextView songTitle;
		TextView artistName;
	}
}
