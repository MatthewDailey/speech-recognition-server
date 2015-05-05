package com.saypenis.speech.api.read;

public interface PageLoader<T> {
	T[] getPage(int page, int pageSize);
}
