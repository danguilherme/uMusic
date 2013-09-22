package com.ventura.umusic.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.ventura.umusic.R;

public class ButtonGroupItem extends Button {

	private final String SHOW_ARROW_ATTRIBUTE_NAME = "showArrow";

	public boolean showArrow = false;

	public ButtonGroupItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		attrs.getAttributeBooleanValue(0, true);

		String showArrow = attrs.getAttributeValue(null,
				this.SHOW_ARROW_ATTRIBUTE_NAME);

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.button_group_item, null);
		View v = View.inflate(context, R.layout.button_group_item, null);

		if (showArrow != null) {
			this.showArrow = Boolean.valueOf(showArrow);
		}
	}
}
