package com.easytravel.easytravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	TextView register;
	EditText email;
	EditText password;
	Button login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_form);
		
		register = (TextView) findViewById(R.id.tv_registerLogin);
		email = (EditText) findViewById(R.id.et_emailLogin);
		password = (EditText) findViewById(R.id.et_passwordLogin);
		login = (Button) findViewById(R.id.btn_login);
		
		if (getIntent().getStringExtra("email") != null && getIntent().getStringExtra("password") != null) {
			email.setText(getIntent().getStringExtra("email"));
			password.setText(getIntent().getStringExtra("password"));
		}
	}

	public void moveToRegisterActivity(View v){
		Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(i);
	}
}
