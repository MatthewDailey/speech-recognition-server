package com.saypenis.speech.api.read;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.http.annotation.ThreadSafe;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@ThreadSafe
public class PagingCache<T> {
	
	private static final int KEY = 1;
	private static final int CACHE_REFRESH_SEC = 1;
	
	private final LoadingCache<Integer, T[]> cache;
	
	

	public PagingCache(final PageLoader<T> pageSupplier, final Supplier<Integer> pageSizeSupplier) {
		this.cache = CacheBuilder.newBuilder()
				.refreshAfterWrite(CACHE_REFRESH_SEC, TimeUnit.SECONDS)
				.build(new CacheLoader<Integer, T[]>() {
			@Override
			public T[] load(Integer key) throws Exception {
				return pageSupplier.getPage(0, pageSizeSupplier.get());
			}
		});
	}
	
	public PageLoader<T> getPageLoader() {
		final T[] cacheArray = getPageFromCache();
		return new PageLoader<T>() {
			@Override
			public T[] getPage(int page, int pageSize) {
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
	
	private T[] getPageFromCache() {
		T[] cacheArray;
		try {
			cacheArray = cache.get(KEY);
		} catch (ExecutionException e) {
			return newEmptyTArray();
		}
		return cacheArray;
	}
	
	@SuppressWarnings("unchecked")
	private T[] newEmptyTArray() {
		return (T[]) new Object[0];
	}
}
