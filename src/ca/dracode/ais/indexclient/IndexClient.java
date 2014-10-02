/*******************************************************************************
 * Copyright 2014 Benjamin Winger.
 *
 * This file is part of AIS Client Library.
 *
 * AIS Client Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AIS Client Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with AIS Client Library.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package ca.dracode.ais.indexclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.dracode.ais.indexdata.PageResult;
import ca.dracode.ais.service.BSearchService1_0;

public class IndexClient {
    /**
     * ID for a search that requires the exact text input to exist
     */
	public static final int QUERY_BOOLEAN = 0;
    /**
     * ID for a fuzzy search
     */
	public static final int QUERY_STANDARD = 1;
	private static String TAG = "ca.dracode.ais.indexclient.IndexClient";
	private BSearchService1_0 mService = null;
	private boolean mIsBound;
	private List<String> filePaths;
    private int id;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = BSearchService1_0.Stub.asInterface(service);
            try {
                id = mService.getId();
            } catch(RemoteException e){
                Log.e(TAG, "Error", e);
            }
            listener.connectedToService();
            Log.i(TAG, "Service: " + mService);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "Service has unexpectedly disconnected");
            listener.disconnectedFromService();
			mService = null;
		}
	};
    private Thread t;
    private IndexListener listener;

    /**
     * @param listener The activity that implements IndexListener to react to search results
     * @param context
     */
    public IndexClient(IndexListener listener, Context context) {
        this.listener = listener;
        this.filePaths = new ArrayList<String>();
		doBindService(context);
	}

    /**
     * @param dir Directory inside which the service file will be created
     * @param name The name of the service that the file describes
     * @param extensions The extensions that the service file will contain
     * @param fileName The name of the service file
     */
    public static void createServiceFile(String dir, String name,
                                         List<String> extensions, String fileName) {
        if(fileName == null) {
            fileName = "Service";
        }
        Log.i(TAG, "Creating folder: " + dir + new File(dir).mkdirs());
        if(!new File(dir + "/" + fileName + ".is").exists()) {
            BufferedWriter bw = null;
            try {

                bw = new BufferedWriter(new FileWriter(dir + "/Service.is"));
            } catch(IOException e) {
                Log.e(TAG, "Error while creating writer: ", e);
                e.printStackTrace();
            }
            if(bw != null) {
                try {
                    bw.write(name);
                    bw.newLine();
                    for(int i = 0; i < extensions.size(); i++) {
                        bw.write(extensions.get(i));
                        bw.newLine();
                    }
                    bw.close();
                } catch(IOException e) {
                    Log.e(TAG, "Error while writing: ", e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Checks if the client is still connected to the service
     * @return true if the client has connected to the indexing service, false otherwise
     */
    public boolean isServiceConnected() {
        return mIsBound;
    }

    private void doBindService(Context c) {
        // Establish a connection with the service.
        Log.i(TAG, "Binding to service...");
		mIsBound = c.bindService(new Intent(
						"ca.dracode.ais.service.IndexService.SEARCH"), mConnection,
				Context.BIND_AUTO_CREATE
		);
		Log.i(TAG, "Service is bound = " + mIsBound);
	}

    /**
     * Tells the search service to unload the files loaded by this IndexClient and disconnects
     * from the SearchService
     * @param context
     */
    public void close(Context context) {
        for(String s : filePaths) {
            this.unloadIndex(s);
        }
		this.doUnbindService(context);
	}

    /**
     * Disconnects the client from the SearchService
     * @param context
     */
	private void doUnbindService(Context context) {
		if (mIsBound) {
			// Detach our existing connection.
			context.unbindService(mConnection);
			mIsBound = false;
		}
	}

    /**
     * Tells the searchService to build a specific file
     * <p>
     *     To be used only in the case that the file is not already in the index
     *     In most cases the file will have already been indexed by the indexer.
     * </p>
     * @param filePath Path of the file to be indexed
     * @param contents Contents of the file split by page in the form of an List
     */
    public void buildIndex(final String filePath,
                           final List<String> contents) {
		/** TODO - Tell client if the index is unbuildable due to the lock being in place.
		 * 		Will not be able to send Strings in contents that are larger than 256KB 
		 * 		In the event of this, it should either try to send them as-is and hope it 
		 * 		does not crash (theoretically it should be able to send up to 1MB, but I found
		 * 		that sending that much would cause a crash), or split up the page into multiple parts and send them separately
		 * **/

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					int length = contents.size();
					for (int i = 0; i < length; i++) {
						int size = 0;
						ArrayList<String> tmp = new ArrayList<String>();
						int init = i;
						while (i < length && size + contents.get(i).getBytes().length < 256 * 1024) {
							tmp.add(contents.get(i));
							size += contents.get(i).getBytes().length;
							i++;
						}
						Log.e(TAG, "Size: " + size + " iterator: " + i);
						mService.buildIndex(filePath, tmp, init, length);
					}
					if (listener != null) {
						listener.indexCreated(filePath);
					}
				} catch (RemoteException e) {
					Log.e(TAG, "Error while closing buffered reader", e);
				}
			}
		}).start();
	}

    /**
     * Tells the SearchService to load the index at filePath
     * @param filePath The path of the file to be loaded
     */
    public void loadIndex(final String filePath) {
        new Thread(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG, "Listener = " + listener + " Bound: " + mIsBound
						+ " Service " + mService);
				try {
					if (listener != null) {
						Log.e(TAG, "Trying to load from service " + mService);
						listener.indexLoaded(filePath, mService.load(filePath));
					} else {
						mService.load(filePath);
					}
				} catch (RemoteException e) {
					Log.e(TAG, "Error while loading remote service", e);
				}
			}
		}).start();
        this.filePaths.add(filePath);
	}

    /**
     * Tells the SearchService to unload the file
     * @param filePath Path of the file to be unloaded
     */
    public void unloadIndex(final String filePath) {
        new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listener.indexUnloaded(filePath, mService.unload(filePath));
				} catch (RemoteException e) {
					Log.e(TAG, "Error while communicating with remote service", e);
				}
			}
		}).start();
        this.filePaths.remove(filePath);
	}

    /**
     * Calls for a single file search
     * @param text Text search query
     * @param filePath Path of the file to be searched
     * @param kill If true, cancels previous searches before searching
     */
    public void search(final String text, final String filePath, boolean kill) {
        this.search(text, IndexClient.QUERY_STANDARD, filePath, 0, 10, 0, kill);
	}

    /**
     * Calls for a single file search
     * @param text Text search query
     * @param filePath Path of the file to be searched
     * @param hits Maximum results to be generated
     * @param kill If true, cancels previous searches before searching
     */
    public void search(final String text, final String filePath, int hits, boolean kill) {
        this.search(text, IndexClient.QUERY_STANDARD, filePath, 0, hits, 0, kill);
	}

    /**
     * Cancels any searches that are in operation
     */
    public void cancelSearch(int id) {
        try {
            mService.interrupt(id);
		} catch (RemoteException e){
			Log.e(TAG, "Error while canceling search", e);
		}
	}

    /**
     * Calls for a single file search
     * @param text Text search query
     * @param type Type of search; one of QUERY_BOOLEAN or QUERY_STANDARD
     * @param filePath Path of the file to be searched
     * @param page Page on which to begin the search
     * @param hits Maximum results to be generated
     * @param set Set number, where searching for set n gives results from set*hits to set*hits +
     *            hits
     * @param kill If true, cancels previous searches before searching
     */
    public void search(final String text, final int type, final String filePath, final int page,
                       final int hits, final int set, boolean kill) {
		if(kill && t != null){
			cancelSearch(id);
			try {
				while (t != null) Thread.sleep(1);
			} catch (InterruptedException e){
				Log.e(TAG, "", e);
			}
		}
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.i(TAG, "Searching for " + text);
					PageResult[] results = mService.find(id, filePath, type, text, hits, page, set);
					if(results != null)
					listener.searchCompleted(text, results);
					Log.i(TAG, "Done Searching for " + text);
				} catch (RemoteException e) {
					Log.e(TAG, "Error while communicating with remote service", e);
					listener.errorWhileSearching(text, filePath);
				}
				t = null;
			}
		});
		t.start();
	}

    /**
     * Calls for a single file search
     * @param text Text search query
     * @param type Type of search; one of QUERY_BOOLEAN or QUERY_STANDARD
     * @param filePath Path of the file to be searched
     * @param hits Maximum results to be generated
     * @param set Set number, where searching for set n gives results from set*hits to set*hits +
     *            hits
     * @param kill If true, cancels previous searches before searching
     */
    public void searchIn(final String text, final int type, final List<String> filePath,
                         final int hits, final int set, boolean kill){
		if(kill && t != null){
			cancelSearch(id);
			try {
				while (t != null) Thread.sleep(1);
			} catch (InterruptedException e){
				Log.e(TAG, "", e);
			}
		}
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.i(TAG, "Searching for " + text);
					PageResult[] results = mService.findIn(id, filePath, type, text, hits, set);
					if(results != null) {
						listener.searchCompleted(text, results);
					}
					Log.i(TAG, "Done Searching for " + text);
				} catch (RemoteException e) {
					Log.e(TAG, "Error while communicating with remote service", e);
					listener.errorWhileSearching(text, filePath.toString());
				}
				t = null;
			}
		});
		t.start();
	}

    /**
     * Calls for a multi-file search
     * @param text Text search query
     * @param type Type of search; one of QUERY_BOOLEAN or QUERY_STANDARD
     * @param filePath Path of the file to be searched
     * @param hits Maximum results to be generated
     * @param set Set number, where searching for set n gives results from set*hits to set*hits +
     *            hits
     * @param kill If true, cancels previous searches before searching
     */
    public void searchInPath(final String text, final int type, final List<String> filePath,
                             final int hits, final int set, boolean kill){
		if(kill && t != null){
			cancelSearch(id);
			try {
				while (t != null) Thread.sleep(1);
			} catch (InterruptedException e){
				Log.e(TAG, "", e);
			}
		}
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.i(TAG, "Searching for " + text);
					List<String> list = mService.findName(id, filePath, type, text, hits, set);
					if(list != null)
					listener.searchCompleted(text, list);
					Log.i(TAG, "Done Searching for " + text);
				} catch (RemoteException e) {
					Log.e(TAG, "Error while communicating with remote service", e);
					listener.errorWhileSearching(text, filePath.toString());
				}
				t = null;
			}
		});
		t.start();
	}
}
