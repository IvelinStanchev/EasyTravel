package com.easytravel.easytravel;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class NoInternetConnectionFragment extends Fragment {
	
	private static final String NO_INTERNET_CONNECTION_MESSAGE = "No Internet Connection!";
	
	private Button mTryAgain;
	private Fragment mCalledFragment;
	private InternetConnection mInternetConnection;
	
	public NoInternetConnectionFragment(){
	}
	
	public NoInternetConnectionFragment(Fragment calledFragment) {
		this.mCalledFragment = calledFragment;
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_no_internet_connection,
				container, false);

		mInternetConnection = new InternetConnection(getActivity());
		mTryAgain = (Button) rootView.findViewById(R.id.btn_try_again);
		mTryAgain.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mInternetConnection.isNetworkAvailable()) {
					Fragment fragment = mCalledFragment;
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();
				}
				else{
					Toast.makeText(getActivity(), NO_INTERNET_CONNECTION_MESSAGE, Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rootView;
	}
	
}
