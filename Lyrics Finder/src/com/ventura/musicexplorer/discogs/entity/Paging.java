package com.ventura.musicexplorer.discogs.entity;

import java.net.URL;

import com.google.gson.annotations.SerializedName;

public class Paging {
	public static final String KEY_PAGINATION_ROOT = "pagination";

	public static final String KEY_PER_PAGE = "per_page";
	public static final String KEY_ITEMS_COUNT = "items";
	public static final String KEY_ACTUAL_PAGE = "page";
	public static final String KEY_PAGES_COUNT = "pages";
	public static final String KEY_PAGES_ADDRESSES = "urls";

	@SerializedName(KEY_ACTUAL_PAGE)
	private int actualPage;
	@SerializedName(KEY_PER_PAGE)
	private int itemsPerPage = 50;
	@SerializedName(KEY_ITEMS_COUNT)
	private int itemsCount;
	@SerializedName(KEY_PAGES_COUNT)
	private int pagesCount;
	@SerializedName(KEY_PAGES_ADDRESSES)
	private PageNavigation pageNavigation;

	public boolean isLast() {
		return this.actualPage == this.pagesCount;
	}

	public boolean isFirst() {
		return this.actualPage == 1;
	}

	public int getActualPage() {
		return actualPage;
	}

	public void setActualPage(int actualPage) {
		this.actualPage = actualPage;
	}

	public int getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public int getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(int itemsCount) {
		this.itemsCount = itemsCount;
	}

	public int getPagesCount() {
		return pagesCount;
	}

	public void setPagesCount(int pagesCount) {
		this.pagesCount = pagesCount;
	}

	public PageNavigation getPageNavigation() {
		return pageNavigation;
	}

	public void setPageNavigation(PageNavigation pageNavigation) {
		this.pageNavigation = pageNavigation;
	}

	@Override
	public String toString() {
		return "Page " + this.actualPage + " of " + this.pagesCount;
	}

	public class PageNavigation {
		public static final String KEY_PREV_PAGE = "prev";
		public static final String KEY_NEXT_PAGE = "next";
		public static final String KEY_LAST_PAGE = "last";
		public static final String KEY_FIRST_PAGE = "first";

		@SerializedName(KEY_PREV_PAGE)
		private URL previousPageUrl;
		@SerializedName(KEY_NEXT_PAGE)
		private URL nextPageUrl;
		@SerializedName(KEY_LAST_PAGE)
		private URL lastPageUrl;
		@SerializedName(KEY_FIRST_PAGE)
		private URL firstPageUrl;

		public URL getPreviousPageUrl() {
			return previousPageUrl;
		}

		public void setPreviousPageUrl(URL previousPageUrl) {
			this.previousPageUrl = previousPageUrl;
		}

		public URL getNextPageUrl() {
			return nextPageUrl;
		}

		public void setNextPageUrl(URL nextPageUrl) {
			this.nextPageUrl = nextPageUrl;
		}

		public URL getLastPageUrl() {
			return lastPageUrl;
		}

		public void setLastPageUrl(URL lastPageUrl) {
			this.lastPageUrl = lastPageUrl;
		}

		public URL getFirstPageUrl() {
			return firstPageUrl;
		}

		public void setFirstPageUrl(URL firstPageUrl) {
			this.firstPageUrl = firstPageUrl;
		}
	}

}
