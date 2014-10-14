package com.easytravel.easytravel;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NoInternetAccessFragment extends Fragment implements
		OnClickListener {

	private Button btn_NetAccess;

	public NoInternetAccessFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.no_internet_access,
				container, false);

		btn_NetAccess = (Button) rootView.findViewById(
				R.id.btn_check_internet_access);

		btn_NetAccess.setOnClickListener(this);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!isNetworkAvailable()) {
			Toast.makeText(getActivity(), "Still there is no internet connectivity!", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Fragment fragment = new HomeFragment();

			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(R.id.frame_container, fragment)
						.commit();
			}
		}
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}