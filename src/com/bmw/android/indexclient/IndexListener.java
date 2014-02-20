/**
 *  Copyright 2014 Benjamin Winger
 *  
 *  This file is part of The Android Indexing Service Client Library.
 *
 *   The Android Indexing Service Client Library is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   The Android Indexing Service Client Library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with The Android Indexing Service Client Library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bmw.android.indexclient;

import java.util.List;

import com.bmw.android.indexdata.PageResult;

public interface IndexListener {
	public void indexCreated(String path);
	public void indexLoaded(String path, int loaded);
	public void indexUnloaded(String path, boolean unloaded);
	public void searchCompleted(String text, PageResult[] pageResults);
	public void errorWhileSearching(String text, String index);
}
