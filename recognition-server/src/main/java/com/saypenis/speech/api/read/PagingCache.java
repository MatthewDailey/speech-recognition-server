package com.saypenis.speech.api.read;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.http.annotation.ThreadSafe;

import com.google.common.base.Supplier;

@ThreadSafe
public class PagingCache<T> {

	public PagingCache(PageLoader<T> pageSupplier, Supplier<Integer> pageSizeSupplier) {
		this.pageSupplier = pageSupplier;
		this.pageSizeSupplier = pageSizeSupplier;
	}
	
	private final Supplier<Integer> pageSizeSupplier;
	private final PageLoader<T> pageSupplier;
	
	private final AtomicReference<T[]> cachedPages = new AtomicReference<T[]>(); 
				
	public PageLoader<T> getPageLoader() {
		return new PageLoader<T>() {
			@Override
			public T[] getPage(int page, int pageSize) {
				T[] cacheArray = getCacheArray();
				int startIndexInclusive = getStart(page, pageSize);
				int endIndexExclusive = getEndIndex(cacheArray.length, page, pageSize);
				if (startIndexInclusive < cacheArray.length) {
					return Arrays.copyOfRange(cacheArray, startIndexInclusive, endIndexExclusive);	
				} else {
					return newEmptyTArray();
				}
			}
			
			private int getStart(int page, int pageSize) {
				return page * pageSize;
			}
			
			private int getEndIndex(int arrayLength, int page, int pageSize) {
				return Math.min(arrayLength, getStart(page, pageSize) + pageSize);
			}
		};
	}

	private T[] getCacheArray() {
		T[] cacheArray = cachedPages.get();
		if (cacheArray == null) {
			refresh();
			cacheArray = cachedPages.get();
		}
		return cacheArray;
	}
	
	public void refresh() {
		cachedPages.set(pageSupplier.getPage(0, pageSizeSupplier.get()));
	}
	
	@SuppressWarnings("unchecked")
	private T[] newEmptyTArray() {
		return (T[]) new Object[0];
	}
}
