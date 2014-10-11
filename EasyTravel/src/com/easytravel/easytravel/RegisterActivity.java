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

public class RegisterActivity extends Activity implements OnClickListener{

	EditText email;
	EditText password;
	EditText confirmPassword;
	RadioButton isDriver;
	RadioButton isNotDriver;
	EditText carData;
	Button registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_form);

		email = (EditText) findViewById(R.id.et_email);
		password = (EditText) findViewById(R.id.et_password);
		confirmPassword = (EditText) findViewById(R.id.et_confirmPassword);
		isDriver = (RadioButton) findViewById(R.id.rb_isDriver);
		isNotDriver = (RadioButton) findViewById(R.id.rb_isNotDriver);
		carData = (EditText) findViewById(R.id.et_carData);
		registerButton = (Button) findViewById(R.id.btn_register);

		registerButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (email.getText().length() <= 3) {
			Toast.makeText(RegisterActivity.this,
					"Your email should be at least 4 symbols!",
					Toast.LENGTH_SHORT).show();
		} else if (password.getText().length() < 6) {
			Toast.makeText(RegisterActivity.this,
					"Your password should be at least 6 symbols!",
					Toast.LENGTH_SHORT).show();
		} else if (!String.valueOf(confirmPassword.getText()).equals(String.valueOf(password.getText()))) {
			Toast.makeText(RegisterActivity.this, "Passwords mismatch!",
					Toast.LENGTH_SHORT).show();
		} else if (isDriver.isChecked() && carData.getText().length() < 4) {
			Toast.makeText(RegisterActivity.this,
					"Your car data information should be at least 3 symbols!",
					Toast.LENGTH_SHORT).show();
		} else if (isDriver.isChecked() && carData.getText().length() > 100) {
			Toast.makeText(
					RegisterActivity.this,
					"Your car data information should be smaller than 100 symbols!",
					Toast.LENGTH_SHORT).show();
		} else {
			Register register = new Register();
			register.execute(String.valueOf(email.getText()),
					String.valueOf(password.getText()),
					String.valueOf(confirmPassword.getText()),
					String.valueOf(isDriver.isChecked()),
					String.valueOf(carData.getText()));
		}

	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.rb_isDriver:
			if (checked) {
				// show
				carData.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rb_isNotDriver:
			if (checked) {
				// not show
				carData.setVisibility(View.INVISIBLE);
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
			HttpPost httppost = new HttpPost(
					"http://spa2014.bgcoder.com/api/users/register");

			httppost.setHeader("Content-type", "application/json");

			try {
				httppost.setEntity(new StringEntity(object.toString()));
			} catch (UnsupportedEncodingException e1) {
				Log.d("D1", e1.toString());
			}

			try {
				HttpResponse response = httpClient.execute(httppost);
				
				Log.d("D1", "Registered");
				
				return response;
			} catch (Exception e) {
				Log.d("D1", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				Toast.makeText(
						RegisterActivity.this,
						"Successfully registered!",
						Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
				i.putExtra("email", String.valueOf(email.getText()));
				i.putExtra("password", String.valueOf(password.getText()));
				startActivity(i);
			}
			else if (response.getStatusLine().getStatusCode() == 400){
				Toast.makeText(
						RegisterActivity.this,
						"Users with the same name already exists!",
						Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
