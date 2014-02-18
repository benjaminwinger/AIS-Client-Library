package com.bmw.android.indexclient;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.bmw.android.indexservice.BSearchService;

public class IndexClient {
	private static String TAG = "com.bmw.android.indexclient.IndexClient";
	public static final int QUERY_BOOLEAN = 0;
	public static final int QUERY_STANDARD = 1;
	private BSearchService mService = null;
	private boolean mIsBound;
	private String filePath;

	private IndexListener listener;


	public IndexClient(IndexListener listener, final Context c, String filePath) {
		this.listener = listener;
		this.filePath = filePath;
		doBindService(c);
	}

	public static void createServiceFile(String dir, String name,
			List<String> extensions) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(dir + "/Service.is"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bw != null) {
			try {
				bw.write(name);
				bw.newLine();
				for (int i = 0; i < extensions.size(); i++) {
					bw.write(extensions.get(i));
					bw.newLine();
				}
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void doBindService(Context c) {
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		Log.i(TAG, "Binding to service...");
		mIsBound = c.bindService(new Intent(
				"com.bmw.android.indexservice.SEARCH"), mConnection,
				Context.BIND_AUTO_CREATE);
		Log.i(TAG, "Service is bound = " + mIsBound);
	}

	public void doUnbindService(Context c) {
		if (mIsBound) {
			// Detach our existing connection.
			c.unbindService(mConnection);
			mIsBound = false;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the service is established
		public void onServiceConnected(ComponentName className, IBinder service) {
			// Following the example above for an AIDL interface,
			// this gets an instance of the IRemoteInterface, which we can use
			// to call on the service
			mService = BSearchService.Stub.asInterface(service);
			Log.i(TAG, "Service: " + mService);
			loadIndex(filePath);
		}

		// Called when the connection with the service disconnects unexpectedly
		public void onServiceDisconnected(ComponentName className) {
			Log.e(TAG, "Service has unexpectedly disconnected");
			mService = null;
		}
	};

	public void buildIndex(final String filePath,
			final ArrayList<String> contents) {
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
						while(i < length && size + contents.get(i).getBytes().length < 256*1024){
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void unloadIndex(final String filePath) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listener.indexUnloaded(filePath, mService.unload(filePath));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void search(final String text, final String filePath){
		this.search(text, filePath, 0, 10);
	}
	
	public void search(final String text, final String filePath, int hits){
		this.search(text, filePath, 0, hits);
	}

	public void search(final String text, final String filePath, final int page, final int hits) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Log.i(TAG, "Searching for " + text);
					listener.searchCompleted(text, mService.find(filePath, IndexClient.QUERY_BOOLEAN, text, hits, page));
					Log.i(TAG, "Done Searching for " + text);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					listener.errorWhileSearching(text, filePath);
				}
			}
		}).start();
	}
}
