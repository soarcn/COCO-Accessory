package com.cocosw.accessory.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 *
 */
public class NetworkConnectivity {

	private String ip;
	private final Context context;
	private final WeakHashMap<NetworkMonitorListener,Void> networkMonitorListeners = new WeakHashMap<NetworkMonitorListener,Void>();
	private NetworkInfo networkInfo;
	private boolean isWifi;
    private static NetworkConnectivity instance;
    private BroadcastReceiver mNetworkStateIntentReceiver;
    private ConnectivityManager cm;

    private NetworkConnectivity(final Context ctx) {
		context = ctx;
		networkInfo = null;
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
		refresh(null);
        start();
	}


    public static NetworkConnectivity getInstance(final Context ctx) {
        if (instance == null)
             instance = new NetworkConnectivity(ctx);
        return instance;
    }

    public static NetworkConnectivity getInstance() {
        if(instance==null)
            throw new IllegalAccessError("InitConnectivity fist");
        return instance;
    }

	public void addNetworkMonitorListener(final NetworkMonitorListener l) {
		networkMonitorListeners.put(l,null);
		notifyNetworkMonitorListener(l);
	}

	public void removeNetworkMonitorListener(final NetworkMonitorListener l) {
		networkMonitorListeners.remove(l);
	}

	/**
	 * A synchronous call to check if network connectivity exists.
	 * 
	 * @return {@true} if network is connected, {@false} otherwise.
	 */
	public boolean isConnected() {
		if (networkInfo != null
				&& networkInfo.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get instance network connectivity status
	 * 
	 * @return
	 */
	public boolean checkConnected() {
		refresh(null);
		return isConnected();
	}

	private void notifyNetworkMonitorListener(final NetworkMonitorListener l) {
		if (networkInfo == null) {
			l.connectionLost();
			return;
		}
		if (networkInfo.isConnected()) {
			l.connectionEstablished();
		} else if (networkInfo.isConnectedOrConnecting()) {
			l.connectionCheckInProgress();
		} else {
			l.connectionLost();
		}
	}

	private void notifyNetworkMonitorListeners() {
        Iterator<NetworkMonitorListener> keys = networkMonitorListeners.keySet().iterator();

        while(keys.hasNext()){
            NetworkMonitorListener d = keys.next();
            if (d!=null)
            notifyNetworkMonitorListener(d);
        }
	}

	/**
	 * get current network Name/Type
	 *
	 * @return network Name/Type
	 */
	public String getNetworkType() {
		if (networkInfo != null) {
			return networkInfo.getExtraInfo();
		} else {
			return null;
		}
	}

	public boolean isWifi() {
		return isWifi;
	}

	public boolean checkWifi() {
		isWifi = false;
		final ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
		for (final NetworkInfo networkInfo : networkInfos) {
			if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
				if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					isWifi = false;
				}
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					isWifi = true;
				}
			}
		}
		return isWifi;
	}

	/**
     * Get current local ip
     *
	 * @return
	 */
	public String getLocalIpAddress() {
		if (ip == null) {
			ip = NetworkConnectivity.getInstance().getIp();
		}
		return "127.0.0.1";
	}

	private String getIp() {
		try {
			for (final Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				final NetworkInterface intf = en.nextElement();
				for (final Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					final InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (final Exception e) {
		}
		return "127.0.0.1";
	}

	/**
     * refresh status base on the networkInfo
     *
	 * @param networkInfo
	 */
	private void refresh(final NetworkInfo networkInfo) {
		if (networkInfo == null) {
			cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			this.networkInfo = cm.getActiveNetworkInfo();
		} else {
            this.networkInfo = networkInfo;
		}
		ip = getIp();
		checkWifi();
		notifyNetworkMonitorListeners();
	}


    private void start() {
        IntentFilter mNetworkStateChangedFilter = new IntentFilter();
        mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (mNetworkStateIntentReceiver!=null) {
            stop();
        }
        mNetworkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    final NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    networkInfo = cm.getNetworkInfo(info.getType());
                    refresh(networkInfo);
                }
            }
        };
        context.registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
    }

    public void stop() {
        context.unregisterReceiver(mNetworkStateIntentReceiver);
        mNetworkStateIntentReceiver=null;
    }


    public static interface NetworkMonitorListener {

        /**
         * connection established
         */
        public void connectionEstablished();

        /**
         * connection lost
         */
        public void connectionLost();

        /**
         * connecting to network
         */
        public void connectionCheckInProgress();
    }


}
