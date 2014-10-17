package com.easytravel.easytravel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class GetStatsRequest extends AsyncTask<Void, Void, HttpResponse> {

	private final String STATS_URL = "http://spa2014.bgcoder.com/api/stats";
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_BAD_REQUEST = 400;
	private Context mContext;
	
	public Context getContext() {
		return mContext;
	}
	
	public void setContext(Context context) {
		this.mContext = context;
	}
	
	public GetStatsRequest(Context context) {
		this.setContext(context);
	}
	
	@Override
	protected void onPostExecute(HttpResponse response) {
		super.onPostExecute(response);

		StringBuilder result = new StringBuilder();
		try {
			InputStream inputStream = response.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String currentRow = null;

			while ((currentRow = bufferedReader.readLine()) != null) {
				result.append(currentRow);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			JSONObject jsonbject = new JSONObject(result.toString());

			if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {

				String trips = jsonbject.getString("trips");
				String finishedTrips = jsonbject.getString("finishedTrips");
				String users = jsonbject.getString("users");
				String drivers = jsonbject.getString("drivers");
				
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getContext());
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("stats_trips", trips);
				editor.putString("stats_finished_trips", finishedTrips);
				editor.putString("stats_users", users);
				editor.putString("stats_drivers", drivers);
				editor.apply();
				
				Intent i = new Intent(getContext(), EasyTravelStatistics.class);
				getContext().startActivity(i);
				
			} else if (response.getStatusLine().getStatusCode() == STATUS_CODE_BAD_REQUEST) {
				Toast.makeText(getContext(),
						jsonbject.getString("message"), Toast.LENGTH_SHORT)
						.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(
				STATS_URL);

		try {
			HttpResponse response = httpClient.execute(httpget);

			return response;
		} catch (Exception e) {
			Log.d("D3", e.toString() + e.toString());
		}

		return null;
	}
}
