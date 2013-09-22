package com.ventura.umusic.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ventura.umusic.R;

public class ButtonGroup extends LinearLayout {

	final String TAG = getClass().getName();

	private final String MESSAGE_ERROR_ONLY_BUTTONS_ALLOWED = "Only buttons are allowed in a ButtonGroup. REMEMBER! YOU CREATED IT!";
	private final String MESSAGE_ERROR_ONLY_ADDVIEWS_METHOD_ALLOWED = "Only addViews(List<Button>) method works well in a ButtonGroup."
			+ " This method will remove all siblings and add the view you are adding.";

	private final int BUTTON_PADDING_LEFT = 8;
	private final int BUTTON_PADDING_TOP = 12;
	private final int BUTTON_PADDING_RIGHT = 8;
	private final int BUTTON_PADDING_BOTTOM = 12;

	private TextView mTitleTextView;
	private String title;
	private final int TITLE_ID = -2;

	public ButtonGroup(Context context) {
		super(context);
		this.setOrientation(LinearLayout.VERTICAL);
		this.buildGroupTitle();
	}

	LinearLayout view;

	public ButtonGroup(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.setPadding(20, 10, 20, 10);
		this.setOrientation(LinearLayout.VERTICAL);

		TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
				R.styleable.ButtonGroup);
		String groupTitle = typedArray
				.getString(R.styleable.ButtonGroup_groupTitle);
		typedArray.recycle();
		
		this.buildGroupTitle();

		if (groupTitle != null && !groupTitle.equals("")) {
			this.setGroupTitle(groupTitle);
		}
	}

	public void addViews(List<Button> children) {
		for (int i = 0; i < children.size(); i++) {
			if (Button.class.equals(children.get(i).getClass())) {
				if (children.get(i).getLayoutParams() == null) {
					children.get(i)
							.setLayoutParams(
									new LayoutParams(
											android.view.ViewGroup.LayoutParams.MATCH_PARENT,
											android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				}

				this.addView(children.get(i), new LayoutParams(children.get(i)
						.getLayoutParams()));
			} else {
				Log.e(TAG, this.MESSAGE_ERROR_ONLY_BUTTONS_ALLOWED);
			}
		}
		refreshButtonGroupPanel();
	}

	/**
	 * Only <code>addViews</code> method works well in a ButtonGroup. This
	 * method will delete all the buttons already in the component and add the
	 * button in parameter, if it's not a button
	 * 
	 * @param child
	 *            If the child is a button, it will be added with the already
	 *            existing ones. Otherwise, the old items will be removed and
	 *            the view will be added
	 */
	@Override
	public void addView(View child) {
		if (child.getClass() == Button.class) {
			this.addButton((Button) child);
		} else {
			Log.w(TAG, MESSAGE_ERROR_ONLY_ADDVIEWS_METHOD_ALLOWED);
			this.removeAllViews();
			super.addView(child);
			refreshButtonGroupPanel();
		}
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		if (Button.class.equals(child.getClass())
				|| child.getId() == ButtonSeparator.SEPARATOR_ID
				|| child.getId() == TITLE_ID) {
			LayoutParams linearLayoutParams = (LayoutParams) params;
			// linearLayoutParams.setMargins(0, 0, 0, 0);
			child.setPadding(BUTTON_PADDING_LEFT, BUTTON_PADDING_TOP,
					BUTTON_PADDING_RIGHT, BUTTON_PADDING_BOTTOM);
			super.addView(child, linearLayoutParams);
		} else {
			Log.e(TAG, this.MESSAGE_ERROR_ONLY_BUTTONS_ALLOWED);
		}
	}

	/**
	 * 
	 * @param button
	 * @return Inserted button
	 */
	public Button addButton(Button button) {
		if (button.getParent() != null) {
			throw new IllegalStateException("The button you want to insert already have a parent");
		}
		
		List<Button> buttons = new ArrayList<Button>();

		// Get the already existing buttons
		for (int i = 0; i < this.getChildCount(); i++) {
			View child = this.getChildAt(i);

			// Verify if it is the title TextView or if it is hidden
			if (child.getClass() == Button.class)
				buttons.add((Button) child);
		}

		buttons.add(button);

		this.removeAllViews();
		this.addViews(buttons);

		return button;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.refreshButtonGroupPanel();
	}

	public void setGroupTitle(String title) {
		this.title = title;
		if (title != null && !title.equals("")) {
			mTitleTextView.setVisibility(View.VISIBLE);
			mTitleTextView.setText(title);
		} else {
			mTitleTextView.setVisibility(View.GONE);
		}
	}

	private void buildGroupTitle() {
		if (mTitleTextView == null || mTitleTextView.getParent() == null) {
			mTitleTextView = new TextView(this.getContext());

			LinearLayout.LayoutParams layoutParams = new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.bottomMargin = 7;
			mTitleTextView.setLayoutParams(layoutParams);

			mTitleTextView.setVisibility(View.GONE);
			mTitleTextView.setId(TITLE_ID);
			mTitleTextView.setTypeface(null, Typeface.BOLD);
			mTitleTextView.setTextSize(16);
			super.addView(mTitleTextView, 0);	
		}
		
		this.setGroupTitle(title);
	}

	private void refreshButtonGroupPanel() {
		boolean first = true, last = false;
		int childCount = this.getChildCount();

		for (int i = 0; i < childCount; i++, childCount = this.getChildCount()) {
			View child = this.getChildAt(i);

			// Verify if it is the title TextView or if it is hidden
			if (child.getId() == TITLE_ID || child.getVisibility() == View.GONE)
				continue;

			last = (i + 1) == childCount;
			if (first) {
				// If there's only one child
				if (last) {
					// Set its 'class' to single button
					child.setBackgroundResource(R.drawable.button_list_single_selector);
				} else {
					// else set to first button
					child.setBackgroundResource(R.drawable.button_list_first_selector);
					first = false;
				}
			} else if (last) {
				this.getChildAt(childCount - 1).setBackgroundResource(
						R.drawable.button_list_last_selector);
			} else {
				child.setBackgroundResource(R.drawable.button_list_middle_selector);
			}
			// If it's not the last button of the group and if there's not a
			// button separator already
			if (!last
					&& this.getChildAt(i + 1).getClass() != ButtonSeparator.class) {
				this.addView(new ButtonSeparator(getContext()), ++i);
			}
		}
		
		buildGroupTitle();
	}

	private class ButtonSeparator extends TextView {

		public static final int SEPARATOR_ID = -1;

		public ButtonSeparator(Context context) {
			super(context);

			LayoutParams separatorLayoutParams = new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, 2);
			separatorLayoutParams.setMargins(0, 0, 0, 0);
			this.setPadding(0, 0, 0, 0);
			this.setLayoutParams(separatorLayoutParams);
			this.setId(ButtonSeparator.SEPARATOR_ID);
			this.setBackgroundColor(Color.LTGRAY);
		}
	}
}
