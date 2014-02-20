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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author benjamin
 *
 */
public abstract class ClientService extends Service {

	/**
	 * Template
	 */
	/*private final MClientService.Stub mBinder = new MClientService.Stub() {
		public List<String> getWords(String path){
			return this.getWords(path);
		}
	};*/
	
	/**
	 * Load libraries to access the file here so that it only has to be done once.
	 * This will always be the first function called 
	 * @param path - the path of the file to be loaded
	 */
	public abstract void loadFile(String path);
	
	/**
	 * The indexer will query the contents of each page one at a time.
	 * Sending all of the information at once is too large to transfer in some files.
	 * @param page - the page of the file to be returned
	 * @return - A string containing all of the words on the page
	 */
	public abstract String getWordsForPage(int page);
	
	/**
	 * 
	 * @return - the number of pages in the file specified at loadFile(String path)
	 */
	public abstract int getPageCount();
	
	public ClientService() {
		
	}

	@Override
	public IBinder onBind(Intent i) {
		return null;
	}

}
