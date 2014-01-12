package com.ventura.umusic.entity.pagination;

import com.google.gson.annotations.SerializedName;

public class Pagination {
	// paging properties
	@SerializedName("page")
	private int actualPage;
	@SerializedName("per_page")
	private int itemsPerPage = 50;
	@SerializedName("items")
	private int itemsCount;
	@SerializedName("pages")
	private int pagesCount;
	
	public Pagination(int itemsCount, int itemsPerPage, int actualPage, int totalPages){
		this.itemsCount = itemsCount;
		this.itemsPerPage = itemsPerPage;
		this.actualPage = actualPage;
		this.pagesCount = totalPages;
	}
	
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
	
	@Override
	public String toString() {
		return String.format("Page %1$d of %2$d (%3$d items, %4$d per page)",
				getActualPage(), getPagesCount(), getItemsCount(), getItemsPerPage());
	}
}
