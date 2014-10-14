package com.easytravel.easytravel.asynctasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetDriverDetails extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {

		String driverId = params[0];
		String accessToken = params[1];

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(
				"http://spa2014.bgcoder.com/api/drivers/" + driverId);
		httpget.setHeader("Authorization", "Bearer " + accessToken);

		try {
			HttpResponse response = httpClient.execute(httpget);

			HttpEntity entity = response.getEntity();

			String responseFinal = EntityUtils.toString(entity);

			return responseFinal;
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}
}
