package com.ventura.musicexplorer.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.R.id;

@EViewGroup(R.layout.key_value_panel)
public class KeyValuePanel extends LinearLayout {

	@ViewById
	TextView key, value;

	private final String DEFAULT_KEY_DELIMITER = ":";

	private String keyText, valueText;
	private String keyDelimiter = DEFAULT_KEY_DELIMITER;

	public KeyValuePanel(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.KeyValuePanel_);

		keyDelimiter = typedArray
				.getString(R.styleable.KeyValuePanel__keyDelimiter);
		if (keyDelimiter == null) {
			keyDelimiter = DEFAULT_KEY_DELIMITER;
		}
		keyText = typedArray.getString(R.styleable.KeyValuePanel__keyText);
		valueText = typedArray.getString(R.styleable.KeyValuePanel__valueText);
		
		typedArray.recycle();
	}

	public KeyValuePanel(Context context, String key, String value) {
		super(context);
		this.initComponent(key, value, DEFAULT_KEY_DELIMITER);
	}

	public KeyValuePanel(Context context, String key, String value,
			String keyDelimiter) {
		super(context);
		this.initComponent(key, value, keyDelimiter);
	}

	private void initComponent(String key, String value, String keyDelimiter) {
		this.value = ((TextView) findViewById(id.value));
		this.key = ((TextView) findViewById(id.key));

		this.keyDelimiter = keyDelimiter;
		this.setKeyValue(key, value);
	}

	@AfterViews
	public void setViewData() {
		this.setKeyValue(keyText, valueText);
	}

	public String getKey() {
		return keyText;
	}

	public String getValue() {
		return valueText;
	}

	public void setKey(String key) {
		keyText = key;

		if (this.key != null) {
			this.key.setText(keyText + this.keyDelimiter);
		}
	}

	public void setValue(String value) {
		valueText = value;

		if (this.value != null) {
			this.value.setText(valueText);
		}
	}

	public void setKeyValue(String key, String value) {
		this.setKey(key);
		this.setValue(value);
	}

	public void setKeyDelimiter(String keyDelimiter) {
		this.keyDelimiter = keyDelimiter;
	}
}
