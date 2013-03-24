package com.ventura.musicexplorer.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ventura.androidutils.utils.Service;

public abstract class BaseService extends Service {
	final String TAG = getClass().getName();
	private final String KEY_PAGINATED_CONTENT = "Data";

	public final String KEY_DATA = "data";
	public final String KEY_SUCCESS = "success";
	public final String KEY_MESSAGE = "message";

	protected static final String URL_BASE_API = "http://www.musicexplorer.somee.com/api";

	private Gson deserializer;

	public BaseService(Context context) {
		super(context);

		deserializer = new GsonBuilder().setFieldNamingPolicy(
				FieldNamingPolicy.UPPER_CAMEL_CASE).create();
	}

	public <T> T deserialize(String json, Class<T> targetType) {
		return deserializer.fromJson(json, targetType);
	}

	public <T> List<T> deserializeList(JSONArray jsonArray, Class<T> targetType) {
		List<T> list = new ArrayList<T>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(this.deserialize(
						jsonArray.getJSONObject(i).toString(), targetType));
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Get the Data property of a paginated JSON object as string.
	 * 
	 * @param jsonString
	 *            The JSON string to search for the data property
	 * @return The Data array of the JSONObject as a JSONArray
	 */
	public JSONArray extractData(String jsonString) {
		if (jsonString == null)
			return new JSONArray();

		JSONObject object = null;
		try {
			object = new JSONObject(jsonString);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error converting String to JSONObject", e);
		}
		return this.extractData(object);
	}

	/**
	 * Get the Data property of a paginated JSONObject.
	 * 
	 * @param source
	 *            The JSONObject to search the Data property.
	 * @return The Data array of the JSONObject as a JSONArray
	 */
	public JSONArray extractData(JSONObject source) {
		if (source == null)
			return new JSONArray();

		JSONArray array = new JSONArray();
		try {
			array = source.getJSONArray(KEY_PAGINATED_CONTENT);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Error retrieveng data from paginated JSON", e);
		}
		return array;
	}

}
