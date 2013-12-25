package com.bmw.android.indexclient;

import java.util.List;

import com.bmw.android.indexdata.Result;

public interface IndexListener {
	public void indexCreated(String path);
	public void indexLoaded(String path, boolean loaded);
	public void searchCompleted(String text, boolean[] pages);
	public void searchCompleted(String text, List<Result> results);
	public void errorWhileSearching(String text, String index);
}
