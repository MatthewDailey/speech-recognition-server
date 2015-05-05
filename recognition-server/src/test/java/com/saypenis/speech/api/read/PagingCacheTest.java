package com.saypenis.speech.api.read;

import static org.junit.Assert.assertArrayEquals;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Suppliers;

public class PagingCacheTest {

	private Mockery mock;
	
	@Before
	public void before() {
		mock = new Mockery();
	}
	
	@Test
	public void testNoResultsWhenUninitialized() {
		@SuppressWarnings("unchecked")
		final PageLoader<Integer> pageLoader = mock.mock(PageLoader.class);
		final int supplierPageSize = 10;
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader, 
				Suppliers.ofInstance(supplierPageSize));
		
		mock.checking(new Expectations(){{
			oneOf(pageLoader).getPage(0, supplierPageSize);
			will(returnValue(new Integer[]{}));
		}});
		
		PageLoader<Integer> cacheLoader = pagingCache.getPageLoader();
		assertArrayEquals(new Integer[0], cacheLoader.getPage(0, 0));
		assertArrayEquals(new Integer[0], cacheLoader.getPage(0, 10));
		assertArrayEquals(new Integer[0], cacheLoader.getPage(1, 10));
		
		mock.assertIsSatisfied();
	}
	
	@Test
	public void testPaging() {
		@SuppressWarnings("unchecked")
		final PageLoader<Integer> pageLoader = (PageLoader<Integer>) mock.mock(PageLoader.class);
		final int supplierPageSize = 10;
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader, 
				Suppliers.ofInstance(supplierPageSize));
		int pageSize = 2;
		
		mock.checking(new Expectations(){{
			oneOf(pageLoader).getPage(0, supplierPageSize);
			will(returnValue(new Integer[]{0,1,2,3,4}));
		}});
		
		pagingCache.refresh();
		PageLoader<Integer> cacheLoader = pagingCache.getPageLoader();

		
		assertArrayEquals(new Integer[]{0, 1}, cacheLoader.getPage(0, pageSize));
		assertArrayEquals(new Integer[]{2, 3}, cacheLoader.getPage(1, pageSize));
		assertArrayEquals(new Integer[]{4}, cacheLoader.getPage(2, pageSize));
		
		mock.assertIsSatisfied();
	}
	
	@Test
	public void testRefresh() {
		@SuppressWarnings("unchecked")
		final PageLoader<Integer> pageLoader = (PageLoader<Integer>) mock.mock(PageLoader.class);
		final int supplierPageSize = 10;
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader, 
				Suppliers.ofInstance(supplierPageSize));
		
		
		mock.checking(new Expectations(){{
			oneOf(pageLoader).getPage(0, supplierPageSize);
			will(returnValue(new Integer[]{0,1}));
			
			oneOf(pageLoader).getPage(0, supplierPageSize);
			will(returnValue(new Integer[]{0,1,2,3,4}));
		}});

		int pageSize = 6;
		
		pagingCache.refresh();
		PageLoader<Integer> cacheLoader = pagingCache.getPageLoader();
		assertArrayEquals(new Integer[]{0, 1}, cacheLoader.getPage(0, pageSize));
		
		pagingCache.refresh();
		PageLoader<Integer> cacheLoaderAfterRefresh = pagingCache.getPageLoader();
		assertArrayEquals(new Integer[]{0, 1, 2, 3, 4}, cacheLoaderAfterRefresh.getPage(0, pageSize));
		
		mock.assertIsSatisfied();
	}

	@Test
	public void testRefreshIfUnitialized() {
		@SuppressWarnings("unchecked")
		final PageLoader<Integer> pageLoader = (PageLoader<Integer>) mock.mock(PageLoader.class);
		final int supplierPageSize = 10;
		PagingCache<Integer> pagingCache = new PagingCache<Integer>(pageLoader, 
				Suppliers.ofInstance(supplierPageSize));
		
		
		mock.checking(new Expectations(){{
			oneOf(pageLoader).getPage(0, supplierPageSize);
			will(returnValue(new Integer[]{0,1}));
		}});

		int pageSize = 6;
		
		PageLoader<Integer> cacheLoader = pagingCache.getPageLoader();
		assertArrayEquals(new Integer[]{0, 1}, cacheLoader.getPage(0, pageSize));
		
		mock.assertIsSatisfied();
	}
	
}
