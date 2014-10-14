package com.easytravel.easytravel.asynctasks;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.easytravel.easytravel.HomeActivity;
import com.easytravel.easytravel.LoginActivity;

public class Logout extends AsyncTask<String, Void, HttpResponse> {

	protected HttpResponse doInBackground(String... params) {
		String accessTokenLogout = params[0];

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://spa2014.bgcoder.com/api/users/logout");
		httppost.addHeader("Authorization", "Bearer " + accessTokenLogout);

		try {
			HttpResponse response = httpClient.execute(httppost);

			return response;
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}
}
