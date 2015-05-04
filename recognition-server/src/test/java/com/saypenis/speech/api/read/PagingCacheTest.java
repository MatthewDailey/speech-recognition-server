package com.saypenis.speech.api.read;

import static org.junit.Assert.assertArrayEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class PagingCacheTest {

	private Mockery mock;
	
	@Before
	public void before() {
		mock = new Mockery();
	}
	
	@Test
	public void testNoResultsWhenUninitialized() {
		@SuppressWarnings("unchecked")
		PageLoader<Integer> pageLoader = mock.mock(PageLoader.class);
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader);
		
		PageLoader<Integer> cacheLoader = pagingCache.getTopRoundPageLoader();
		assertArrayEquals(new Integer[0], cacheLoader.getPage(0, 0));
		assertArrayEquals(new Integer[0], cacheLoader.getPage(0, 10));
		assertArrayEquals(new Integer[0], cacheLoader.getPage(1, 10));
		
		mock.assertIsSatisfied();
	}
	
	@Test
	public void testPaging() {
		@SuppressWarnings("unchecked")
		final PageLoader<Integer> pageLoader = (PageLoader<Integer>) mock.mock(PageLoader.class);
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader);
		int pageSize = 2;
		
		mock.checking(new Expectations(){{
			oneOf(pageLoader).getPage(0, 2000);
			will(returnValue(new Integer[]{0,1,2,3,4}));
		}});
		
		pagingCache.refreshCache();
		PageLoader<Integer> cacheLoader = pagingCache.getTopRoundPageLoader();

		
		assertArrayEquals(new Integer[]{0, 1}, cacheLoader.getPage(0, pageSize));
		assertArrayEquals(new Integer[]{2, 3}, cacheLoader.getPage(1, pageSize));
		assertArrayEquals(new Integer[]{4}, cacheLoader.getPage(2, pageSize));
		
		mock.assertIsSatisfied();
	}
	
	@Test
	public void testRefresh() {
		@SuppressWarnings("unchecked")
		final PageLoader<Integer> pageLoader = (PageLoader<Integer>) mock.mock(PageLoader.class);
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader);
		int pageSize = 6;
		
		mock.checking(new Expectations(){{
			oneOf(pageLoader).getPage(0, 2000);
			will(returnValue(new Integer[]{0,1}));
			
			oneOf(pageLoader).getPage(0, 2000);
			will(returnValue(new Integer[]{0,1,2,3,4}));
		}});
		
		pagingCache.refreshCache();
		PageLoader<Integer> cacheLoader = pagingCache.getTopRoundPageLoader();
		assertArrayEquals(new Integer[]{0, 1}, cacheLoader.getPage(0, pageSize));
		
		pagingCache.refreshCache();
		PageLoader<Integer> cacheLoaderAfterRefresh = pagingCache.getTopRoundPageLoader();
		assertArrayEquals(new Integer[]{0, 1, 2, 3, 4}, cacheLoaderAfterRefresh.getPage(0, pageSize));
		
		mock.assertIsSatisfied();
	}
}
