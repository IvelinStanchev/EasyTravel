package com.easytravel.easytravel;

import android.app.Activity;
import android.os.Bundle;

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
