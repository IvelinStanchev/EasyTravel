package com.easytravel.easytravel;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.easytravel.easytravel.progressactivities.RegisteringActivity;

public class RegisterActivity extends Activity implements OnClickListener {

	private static final String NO_INTERNET_CONNECTION_MESSAGE = "No Internet Connection!";
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_BAD_REQUEST = 400;
	private static final String SUCCESSFULLY_REGISTERED_MESSAGE = "Successfully registered!";
	private static final String LOW_SYMBOLS_EMAIL_MESSAGE = "Your email should be at least 4 symbols!";
	private static final String LOW_SYMBOLS_PASSWORD_MESSAGE = "Your password should be at least 6 symbols!";
	private static final String PASSWORD_MISMATCH_MESSAGE = "Passwords mismatch!";
	private static final String LOW_SYMBOLS_CAR_DATA_MESSAGE = "Your car data information should be at least 3 symbols!";
	private static final String TOO_MANY_SYMBOLS_CAR_DATA_MESSAGE = "Your car data information should be smaller than 100 symbols!";
	private static final String USERS_SAME_NAME_MESSAGE = "Users with the same name already exists!";
	private static final String REGISTER_URL = "http://spa2014.bgcoder.com/api/users/register";

	EditText mEmail;
	EditText mPassword;
	EditText mConfirmPassword;
	RadioButton mIsDriver;
	RadioButton mIsNotDriver;
	EditText mCarData;
	Button mRegisterButton;
	private InternetConnection mInternetConnection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_form);

		mInternetConnection = new InternetConnection(this);

		mEmail = (EditText) findViewById(R.id.et_email);
		mPassword = (EditText) findViewById(R.id.et_password);
		mConfirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
		mIsDriver = (RadioButton) findViewById(R.id.rb_isDriver);
		mIsNotDriver = (RadioButton) findViewById(R.id.rb_isNotDriver);
		mCarData = (EditText) findViewById(R.id.et_carData);
		mRegisterButton = (Button) findViewById(R.id.btn_register);

		mRegisterButton.setOnClickListener(this);

		if (!mInternetConnection.isNetworkAvailable()) {
			Toast.makeText(RegisterActivity.this,
					NO_INTERNET_CONNECTION_MESSAGE, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_register) {
			if (mInternetConnection.isNetworkAvailable()) {
				if (mEmail.getText().length() <= 3) {
					Toast.makeText(RegisterActivity.this,
							LOW_SYMBOLS_EMAIL_MESSAGE, Toast.LENGTH_SHORT)
							.show();
				} else if (mPassword.getText().length() < 6) {
					Toast.makeText(RegisterActivity.this,
							LOW_SYMBOLS_PASSWORD_MESSAGE, Toast.LENGTH_SHORT)
							.show();
				} else if (!String.valueOf(mConfirmPassword.getText()).equals(
						String.valueOf(mPassword.getText()))) {
					Toast.makeText(RegisterActivity.this,
							PASSWORD_MISMATCH_MESSAGE, Toast.LENGTH_SHORT)
							.show();
				} else if (mIsDriver.isChecked()
						&& mCarData.getText().length() < 4) {
					Toast.makeText(RegisterActivity.this,
							LOW_SYMBOLS_CAR_DATA_MESSAGE, Toast.LENGTH_SHORT)
							.show();
				} else if (mIsDriver.isChecked()
						&& mCarData.getText().length() > 100) {
					Toast.makeText(RegisterActivity.this,
							TOO_MANY_SYMBOLS_CAR_DATA_MESSAGE,
							Toast.LENGTH_SHORT).show();
				} else {
					Intent i = new Intent(RegisterActivity.this,
							RegisteringActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					Register register = new Register();
					register.execute(String.valueOf(mEmail.getText()),
							String.valueOf(mPassword.getText()),
							String.valueOf(mConfirmPassword.getText()),
							String.valueOf(mIsDriver.isChecked()),
							String.valueOf(mCarData.getText()));
				}
			} else {
				Toast.makeText(RegisterActivity.this,
						NO_INTERNET_CONNECTION_MESSAGE, Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
		case R.id.rb_isDriver:
			if (checked) {
				mCarData.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rb_isNotDriver:
			if (checked) {
				mCarData.setVisibility(View.INVISIBLE);
			}

			break;
		}
	}

	private class Register extends AsyncTask<String, Void, HttpResponse> {

		protected HttpResponse doInBackground(String... params) {
			String email = params[0];
			String password = params[1];
			String confirmPassword = params[2];
			String isDriver = params[3];
			String carData = params[4];

			JSONObject object = new JSONObject();
			try {
				object.put("email", email);
				object.put("password", password);
				object.put("confirmPassword", confirmPassword);
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			if (isDriver == "false") {
				try {
					object.put("isDriver", false);
				} catch (Exception e) {
					Log.d("D1", e.toString());
				}
			} else {
				try {
					object.put("isDriver", true);
					object.put("car", carData);
				} catch (Exception e) {
					Log.d("D1", e.toString());
				}
			}

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(REGISTER_URL);

			httppost.setHeader("Content-type", "application/json");

			try {
				httppost.setEntity(new StringEntity(object.toString()));
			} catch (UnsupportedEncodingException e1) {
				Log.d("D1", e1.toString());
			}

			try {
				HttpResponse response = httpClient.execute(httppost);

				return response;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			super.onPostExecute(response);

			if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
				Toast.makeText(RegisterActivity.this,
						SUCCESSFULLY_REGISTERED_MESSAGE, Toast.LENGTH_SHORT)
						.show();

				Intent i = new Intent(RegisterActivity.this,
						LoginActivity.class);
				i.putExtra("email", String.valueOf(mEmail.getText()));
				i.putExtra("password", String.valueOf(mPassword.getText()));
				startActivity(i);
			} else if (response.getStatusLine().getStatusCode() == STATUS_CODE_BAD_REQUEST) {
				Intent i = new Intent(RegisterActivity.this,
						RegisterActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);

				Toast.makeText(RegisterActivity.this, USERS_SAME_NAME_MESSAGE,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
