package com.easytravel.easytravel.progressactivities;

import android.app.Activity;
import android.os.Bundle;

import com.easytravel.easytravel.R;

public class RegisteringActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_progress);
	}

	@Override
	public void onBackPressed() {
	}
}
