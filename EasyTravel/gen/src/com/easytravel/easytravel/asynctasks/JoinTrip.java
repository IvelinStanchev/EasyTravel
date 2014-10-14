package com.easytravel.easytravel.asynctasks;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class JoinTrip extends AsyncTask<String, Void, HttpResponse>{

	@Override
	protected HttpResponse doInBackground(String... params) {
		
		String tripId = params[0];
		String accessToken = params[1];
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPut httpput = new HttpPut(
				"http://spa2014.bgcoder.com/api/trips/" + tripId);
		httpput.setHeader("Authorization", "Bearer " + accessToken);

		try {
			HttpResponse response = httpClient.execute(httpput);

			return response;
//			HttpEntity entity = response.getEntity();
//
//		    String responseFinal = EntityUtils.toString(entity);
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}
}
