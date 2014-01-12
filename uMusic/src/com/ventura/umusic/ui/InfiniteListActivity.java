package com.ventura.umusic.ui;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class InfiniteListActivity extends BaseListActivity implements
		OnScrollListener {
	final String TAG = getClass().getName();

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.i(TAG, firstVisibleItem + "/" + totalItemCount);
		
		// if last item
		if ((firstVisibleItem + visibleItemCount) == totalItemCount)
			this.onListEndAchieved();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_FLING:
			Log.i(TAG, "Scroll State = SCROLL_STATE_FLING");
			break;
		case SCROLL_STATE_IDLE:
			Log.i(TAG, "Scroll State = SCROLL_STATE_IDLE");
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
			Log.i(TAG, "Scroll State = SCROLL_STATE_TOUCH_SCROLL");
			break;
		}
	}

	protected abstract void onListEndAchieved();
}
