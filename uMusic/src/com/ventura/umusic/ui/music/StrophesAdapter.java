package com.ventura.umusic.ui.music;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import com.ventura.androidutils.ui.BaseAdapter;
import com.ventura.umusic.R;

public class StrophesAdapter extends BaseAdapter<String> {
	private final String TAG = getClass().getName();
	private final int LAYOUT_ID = R.layout.list_item_lyrics_strophe;
	private SparseBooleanArray selectedItems = new SparseBooleanArray();

	public StrophesAdapter(Context context, List<String> data) {
		super(context, data);
		this.imageLoader = null; // Will not be necessary
		for (int i = 0; i < data.size(); i++) {
			selectedItems.append(i, false);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(LAYOUT_ID, null);
			viewHolder.strophe = (TextView) convertView
					.findViewById(R.id.lyric_strophe);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.strophe.setText(this.getItem(position).toString());

		return convertView;
	}

	public void setChecked(int position, boolean selected) {
		selectedItems.put(position, selected);
	}

	public void toggleChecked(int position) {
		this.setChecked(position, !selectedItems.get(position));
	}

	public boolean isChecked(int position) {
		return selectedItems.get(position);
	}

	@Override
	public String getId(int position) {
		return String.valueOf(position);
	}

	public class CheckableStrophe implements Checkable {
		private boolean checked = false;
		private String strophe;

		public CheckableStrophe(String strophe) {
			this.strophe = strophe;
		}
		
		@Override
		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		@Override
		public boolean isChecked() {
			return checked;
		}

		@Override
		public void toggle() {
			checked = !checked;
		}
	}

	private static class ViewHolder {
		public TextView strophe;
		public CheckBox selected;
	}
}