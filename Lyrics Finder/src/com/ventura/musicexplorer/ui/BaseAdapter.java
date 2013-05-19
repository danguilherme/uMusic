package com.ventura.musicexplorer.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;

import com.ventura.musicexplorer.util.ImageLoader;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	private Context context;
	protected List<T> data;
	protected static LayoutInflater inflater = null;
	protected ImageLoader imageLoader;
	
	public BaseAdapter(Context context, List<T> data) {
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context.getApplicationContext());
	}

	@Override
	public int getCount() {
		return this.data.size();
	}

	/**
	 * Inserts a set of items into the current dataset,
	 * notifying that the data has been changed
	 * @param data The items to add
	 */
	public void addItems(List<T> data) {
		this.data.addAll(data);
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Gets the item id by its position
	 * @param position The item position to get the id
	 * @return The item id
	 */
	public abstract String getId(int position);
	
	public Context getContext() {
		return context;
	}
}
