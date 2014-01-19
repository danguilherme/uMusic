package com.ventura.umusic.business;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ventura.androidutils.utils.Service;
import com.ventura.umusic.entity.pagination.PaginatedList;
import com.ventura.umusic.entity.pagination.Pagination;

public abstract class BaseService extends Service {
	final String TAG = getClass().getName();
	private static final String KEY_PAGINATED_CONTENT = "data";
	private static final String KEY_PAGINATION_INFO = "pagination";

	protected final String KEY_DATA = "data";
	protected final String KEY_SUCCESS = "success";
	protected final String KEY_MESSAGE = "message";

	protected static final String URL_BASE_API = "http://www.umusic-api.somee.com/api";

	private Gson deserializer;

	public BaseService(Context context) {
		super(context);

		deserializer = new GsonBuilder().create();
	}

	protected <T> T deserialize(String json, Class<T> targetType) {
		return deserializer.fromJson(json, targetType);
	}

	protected <T> List<T> deserializeList(JSONArray jsonArray, Class<T> targetType) {
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
	 * Get the 'data' property of a paginated JSON object artistService string.
	 * 
	 * @param jsonString
	 *            The JSON string to search for the data property
	 * @return The Data array of the JSONObject artistService a JSONArray
	 */
	protected JSONArray extractData(String jsonString) {
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
	 * Get the 'data' property of a paginated JSONObject.
	 * 
	 * @param source
	 *            The JSONObject to search the Data property.
	 * @return The Data array of the JSONObject artistService a JSONArray
	 */
	protected JSONArray extractData(JSONObject source) {
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


	protected <T> PaginatedList<T> deserializePaginatedList(String jsonString, Class<T> targetType) throws JSONException {
		JSONObject entireObject = new JSONObject(jsonString);
		JSONArray data = extractData(entireObject);
		Pagination paging = deserializer.fromJson(entireObject.getJSONObject(KEY_PAGINATION_INFO).toString(), Pagination.class);
		return new PaginatedList<T>(deserializeList(data, targetType), paging);
	}
	
}
