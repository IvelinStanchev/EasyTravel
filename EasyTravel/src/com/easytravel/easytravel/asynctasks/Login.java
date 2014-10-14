package com.easytravel.easytravel.asynctasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class Login  extends AsyncTask<String, Void, HttpResponse> {
	
	protected HttpResponse doInBackground(String... params) {
		String email = params[0];
		String password = params[1];

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://spa2014.bgcoder.com/api/users/login");
		httppost.addHeader("Content-Type", "x-www-form-urlencoded");

		List<NameValuePair> pair = new ArrayList<NameValuePair>();

		pair.add(new BasicNameValuePair("grant_type", "password"));
		pair.add(new BasicNameValuePair("username", email));
		pair.add(new BasicNameValuePair("password", password));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(pair));

			HttpResponse response = httpClient.execute(httppost);

			return response;
		} catch (Exception e) {
			Log.d("D1", e.toString());
		}

		return null;
	}
}
