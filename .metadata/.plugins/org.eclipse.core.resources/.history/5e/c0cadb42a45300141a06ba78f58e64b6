package com.easytravel.easytravel.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class InternetConnectionReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// Make sure it's an event we're listening for ...
		Toast.makeText(context, "receiver anth", Toast.LENGTH_SHORT).show();
		if (!intent.getAction()
				.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)
				&& !intent.getAction().equals(
						ConnectivityManager.CONNECTIVITY_ACTION)
				&& !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			
			return;
		}

		ConnectivityManager cm = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE));

		if (cm == null) {
			return;
		}

		// Now to check if we're actually connected
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnected()) {
			// Start the service to do our thing
			Toast.makeText(context, "Start service", Toast.LENGTH_SHORT).show();
			Intent pushIntent = new Intent(context, ContentCheckService.class);
			String netCon = cm.getActiveNetworkInfo().getState().toString();
			pushIntent.putExtra("networkConnection", netCon);
			context.startService(pushIntent);
		}
	}
}