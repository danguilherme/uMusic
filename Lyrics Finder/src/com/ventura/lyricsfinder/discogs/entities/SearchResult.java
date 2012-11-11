package com.ventura.lyricsfinder.discogs.entities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchResult {
	public static final String KEY_PAGINATION = "per_page";
	public static final String KEY_PAGINATION_PER_PAGE = "per_page";
	public static final String KEY_PAGINATION_ITEMS_COUNT = "items";
	public static final String KEY_PAGINATION_ACTUAL_PAGE = "page";
	public static final String KEY_PAGINATION_URLS = "urls";
	public static final String KEY_PAGINATION_URLS_NEXT = "next";
	public static final String KEY_PAGINATION_URLS_PREV = "prev";
	public static final String KEY_PAGINATION_URLS_LAST = "last";
	public static final String KEY_PAGINATION_URLS_FIRST = "last";
	public static final String KEY_PAGINATION_PAGES_COUNT = "pages";

	public static final String KEY_RESULTS = "results";

	private int actualPage;
	private int itemsCount;
	private int perPage;
	private int pagesCount;
	private URL nextPage;
	private URL prevPage;
	private URL firstPage;
	private URL lastPage;
	private List<SearchItem> results = new ArrayList<SearchItem>();
	private QueryType searchType;

	public SearchResult(QueryType searchType, JSONObject searchData) {
		JSONArray searchResults = new JSONArray();
		try {
			searchResults = searchData.getJSONArray(SearchResult.KEY_RESULTS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.searchType = searchType;
		this.fill(searchResults);
	}

	public void fill(JSONArray resultsArray) {
		switch (this.searchType) {
		case Artist:
			for (int i = 0; resultsArray != null && i < resultsArray.length(); i++) {
				try {
					this.results.add(new SearchItem(QueryType.Artist,
							resultsArray.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			break;

		default:
			break;
		}
	}

	public int getActualPage() {
		return actualPage;
	}

	public void setActualPage(int actualPage) {
		this.actualPage = actualPage;
	}

	public int getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(int itemsCount) {
		this.itemsCount = itemsCount;
	}

	public int getPerPage() {
		return perPage;
	}

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public int getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(int pagesCount) {
		this.pagesCount = pagesCount;
	}

	public URL getNextPage() {
		return nextPage;
	}

	public void setNextPage(URL nextPage) {
		this.nextPage = nextPage;
	}

	public URL getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(URL prevPage) {
		this.prevPage = prevPage;
	}

	public URL getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(URL firstPage) {
		this.firstPage = firstPage;
	}

	public URL getLastPage() {
		return lastPage;
	}

	public void setLastPage(URL lastPage) {
		this.lastPage = lastPage;
	}

	public List<SearchItem> getResults() {
		return results;
	}

	public void setResults(List<SearchItem> results) {
		this.results = results;
	}

	public int getCount() {
		return results.size();
	}

	public QueryType getSearchType() {
		return searchType;
	}

	public void setSearchType(QueryType searchType) {
		this.searchType = searchType;
	}
	
	
}
