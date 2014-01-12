package com.ventura.umusic.entity.pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapping list for paginated content
 * 
 * @author Guilherme
 * 
 * @param <T>
 *            The entity type that is being paginated
 */
public class PaginatedList<T> {
	private Pagination paging;
	private List<T> items;

	public PaginatedList(List<T> initialItems, int itemsPerPage,
			int actualPage, int totalPages) {
		this.items = initialItems;
		this.paging = new Pagination(initialItems.size(), itemsPerPage,
				actualPage, totalPages);
	}

	public PaginatedList(PaginatedList<T> derived) {
		this.items = derived.getItems();
		this.paging = derived.getPaging();
	}

	public PaginatedList(List<T> initialItems, Pagination pagination) {
		this.items = initialItems;
		this.paging = pagination;
	}

	public List<T> getItems() {
		return new ArrayList<T>(items);
	}

	public int getCount() {
		if (items == null)
			return 0;
		else
			return items.size();
	}

	public void addItems(List<T> items) {
		if (this.items == null)
			this.items = new ArrayList<T>();

		this.items.addAll(items);
	}

	public Pagination getPaging() {
		return paging;
	}

	@Override
	public String toString() {
		return this.paging.getActualPage() + "/" + this.paging.getPagesCount();
	}

	/**
	 * Creates an empty paginated list. Can be used to avoid
	 * java.lang.NullPointerException when trying to access its items
	 * 
	 * @return An empty paginated list with the required type.
	 */
	public static <X> PaginatedList<X> empty() {
		return new PaginatedList<X>(new ArrayList<X>(), 50, 1, 1);
	}

	public static <X> PaginatedList<X> empty(Class<X> entityType) {
		return PaginatedList.empty();
	}
}
