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
	private Socket socket;
	private BSearchService mService = null;
	private boolean mIsBound;
	private String filePath;

	private static final int SERVERPORT = 6002;
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
		/*
		 * final String path = filePath.replaceAll(" ", "\\_");
		 * 
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * try { do { InetAddress serverAddr = InetAddress.getLocalHost();
		 * socket = new Socket(serverAddr, SERVERPORT); } while
		 * (socket.isClosed()); PrintWriter out = new PrintWriter(new
		 * BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),
		 * true); out.println("build " + path); out.flush(); ObjectOutputStream
		 * oos = new ObjectOutputStream(socket.getOutputStream());
		 * oos.writeObject(contents); oos.flush(); out.close(); oos.close();
		 * socket.close(); } catch (UnknownHostException e1) {
		 * e1.printStackTrace(); } catch (IOException e1) {
		 * e1.printStackTrace(); }
		 * 
		 * } }).start();
		 */
	}

	public void loadIndex(final String filePath) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.i(TAG, "Listener = " + listener + " Bound: " + mIsBound
						+ " Service " + mService);
				try {
					if (listener != null) {
						boolean loaded = mService.load(filePath);

						listener.indexLoaded(filePath, loaded);
					} else {
						mService.load(filePath);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		/*
		 * final String path = filePath.replaceAll(" ", "\\_"); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * try { do { InetAddress serverAddr = InetAddress.getLocalHost();
		 * socket = new Socket(serverAddr, SERVERPORT); } while
		 * (socket.isClosed()); PrintWriter out = new PrintWriter(new
		 * BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),
		 * true); out.println("load " + path); out.flush(); BufferedReader in =
		 * new BufferedReader( new InputStreamReader(socket.getInputStream()));
		 * String response = null; while (response == null && in.ready()) {
		 * response = in.readLine(); } boolean loaded = false; if (response !=
		 * null) { if (response.equals("load true")) { loaded = true; } } if
		 * (listener != null) listener.indexLoaded(filePath, loaded);
		 * 
		 * out.close(); in.close(); socket.close(); } catch
		 * (UnknownHostException e1) { e1.printStackTrace(); } catch
		 * (IOException e1) { e1.printStackTrace(); }
		 * 
		 * } }).start();
		 */
	}

	public void unloadIndex(final String filePath) {
		/*
		 * final String path = filePath.replaceAll(" ", "\\_"); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * try { do { InetAddress serverAddr = InetAddress.getLocalHost();
		 * socket = new Socket(serverAddr, SERVERPORT); } while
		 * (socket.isClosed()); PrintWriter out = new PrintWriter(new
		 * BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),
		 * true); out.println("unload " + path); out.flush(); out.close();
		 * socket.close(); } catch (UnknownHostException e1) {
		 * e1.printStackTrace(); } catch (IOException e1) {
		 * e1.printStackTrace(); }
		 * 
		 * } }).start();
		 */
	}

	public void search(final String text, final String filePath,
			final int variance) {
		/*
		 * final String path = filePath.replaceAll(" ", "\\_"); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() { try { do { InetAddress serverAddr =
		 * InetAddress.getLocalHost(); socket = new Socket(serverAddr,
		 * SERVERPORT); } while (socket.isClosed()); PrintWriter out = new
		 * PrintWriter(new BufferedWriter( new
		 * OutputStreamWriter(socket.getOutputStream())), true);
		 * out.println("search " + text + " " + path + " " + variance);
		 * out.flush(); ObjectInputStream in = new ObjectInputStream(socket
		 * .getInputStream()); try { List<Result> results = (List<Result>)
		 * in.readObject(); listener.searchCompleted(text, results); } catch
		 * (ClassNotFoundException e) { e.printStackTrace();
		 * listener.errorWhileSearching(text, filePath); } in.close();
		 * out.close(); socket.close(); } catch (UnknownHostException e1) {
		 * e1.printStackTrace(); } catch (IOException e1) {
		 * e1.printStackTrace(); }
		 * 
		 * } }).start();
		 */
	}

	public void quickSearch(final String text, final String filePath) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					listener.searchCompleted(text, mService.find(text));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					listener.errorWhileSearching(text, filePath);
				}
			}
		}).start();
		/*
		 * final String path = filePath.replaceAll(" ", "\\_"); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * try { do { InetAddress serverAddr = InetAddress.getLocalHost();
		 * socket = new Socket(serverAddr, SERVERPORT); } while
		 * (socket.isClosed()); PrintWriter out = new PrintWriter(new
		 * BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),
		 * true); out.println("qsearch " + text + " " + path); out.flush();
		 * ObjectInputStream in = new ObjectInputStream(
		 * socket.getInputStream()); try { boolean[] results = (boolean[])
		 * in.readObject(); listener.searchCompleted(text, results); } catch
		 * (ClassNotFoundException e) { e.printStackTrace();
		 * listener.errorWhileSearching(text, filePath); } in.close();
		 * out.close(); socket.close(); } catch (UnknownHostException e1) {
		 * e1.printStackTrace(); } catch (IOException e1) {
		 * e1.printStackTrace(); }
		 * 
		 * } }).start();
		 */
	}
}
