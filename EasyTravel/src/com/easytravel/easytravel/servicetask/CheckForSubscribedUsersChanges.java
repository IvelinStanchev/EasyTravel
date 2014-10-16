package com.easytravel.easytravel.servicetask;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class CheckForSubscribedUsersChanges extends
		AsyncTask<String, Void, ArrayList<String>> {

	private static final String DRIVERS_URL = "http://spa2014.bgcoder.com/api/drivers";

	@Override
	protected ArrayList<String> doInBackground(String... params) {

		String userName = params[0];
		String accessToken = params[1];

		DefaultHttpClient httpClient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(DRIVERS_URL + "/?page=1&username="
				+ userName);
		httpget.setHeader("Content-Type", "application/json");
		httpget.setHeader("Authorization", "Bearer " + accessToken);

		try {
			HttpResponse response = httpClient.execute(httpget);

			HttpEntity entity = response.getEntity();

			String responseFinal = EntityUtils.toString(entity);

			JSONArray jsonObj = new JSONArray(responseFinal);

			JSONObject c = jsonObj.getJSONObject(0);

			String upcomingTripsCount = String.valueOf(c
					.get("numberOfUpcomingTrips"));
			String allTripsCount = String.valueOf(c.get("numberOfTotalTrips"));

			ArrayList<String> result = new ArrayList<String>();
			result.add(upcomingTripsCount);
			result.add(allTripsCount);

			return result;
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}

}
