package com.saypenis.speech.api.read;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

import org.apache.http.annotation.ThreadSafe;

import com.google.common.annotations.VisibleForTesting;
import com.saypenis.speech.aws.SayPenisConfiguration;

@ThreadSafe
@Singleton
public final class PagingCache<T> {

	// TODO (mdailey): Initialize with dynamo based PageLoader.
	
	@VisibleForTesting
	PagingCache(PageLoader<T> pageSupplier) {
		this.pageSupplier = pageSupplier;
	}
	
	private final PageLoader<T> pageSupplier;
	
	private final AtomicReference<T[]> topRounds = 
			new AtomicReference<T[]>(newEmptyTArray()); 
				
	public PageLoader<T> getTopRoundPageLoader() {
		return new PageLoader<T>() {
			@Override
			public T[] getPage(int page, int pageSize) {
				T[] topRoundsArray = topRounds.get();
				int startIndexInclusive = getStart(page, pageSize);
				int endIndexExclusive = getEndIndex(topRoundsArray.length, page, pageSize);
				if (startIndexInclusive < topRoundsArray.length) {
					return Arrays.copyOfRange(topRoundsArray, startIndexInclusive, endIndexExclusive);	
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
	
	public void refreshCache() {
		topRounds.set(pageSupplier.getPage(0, SayPenisConfiguration.readCacheSize()));
	}

	@SuppressWarnings("unchecked")
	private T[] newEmptyTArray() {
		return (T[]) new Object[0];
	}
}
