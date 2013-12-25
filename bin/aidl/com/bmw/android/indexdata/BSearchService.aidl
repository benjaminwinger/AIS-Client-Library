package com.bmw.android.indexdata;

interface BSearchService {

	boolean[] find(String text);
	boolean buildIndex(String filePath, in List<String> text);
	boolean load(String filePath);
	
}
