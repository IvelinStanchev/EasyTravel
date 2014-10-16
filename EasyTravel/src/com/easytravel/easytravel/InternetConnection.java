package com.easytravel.easytravel;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnection {
	private Context context;

	public InternetConnection(Context context) {
		this.context = context;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
