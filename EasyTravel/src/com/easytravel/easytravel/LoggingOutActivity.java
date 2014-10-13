package com.easytravel.easytravel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

public class LoggingOutActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logout_progress);
	}

	@SuppressLint("ShowToast")
	@Override
	public void onBackPressed() {
	}
}
