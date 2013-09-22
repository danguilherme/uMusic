package com.ventura.umusic.discogs.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ventura.umusic.discogs.entity.enumerator.QueryType;

public class SearchResult {
	public static final String KEY_RESULTS = "results";

	@SerializedName(Paging.KEY_PAGINATION_ROOT)
	private Paging pagination;

	@SerializedName(KEY_RESULTS)
	private List<SearchItem> results = new ArrayList<SearchItem>();
	private QueryType searchType;

	/**
	 * Create an empty SearchResult instance
	 */
	public SearchResult() {
	}

	public SearchResult(QueryType searchType, List<SearchItem> items) {
		this.searchType = searchType;
		this.results = items;
	}

	public Paging getPagination() {
		return pagination;
	}

	public void setPagination(Paging pagination) {
		this.pagination = pagination;
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

	@Override
	public String toString() {
		return this.results.size() + " search items";
	}
}
