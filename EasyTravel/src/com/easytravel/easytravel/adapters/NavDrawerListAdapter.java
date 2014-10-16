package com.easytravel.easytravel.adapters;
import com.easytravel.easytravel.R;
import com.easytravel.easytravel.models.NavDrawerItem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easytravel.easytravel.models.NavDrawerItem;

public class NavDrawerListAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<NavDrawerItem> mNavDrawerItems;
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.mContext = context;
		this.mNavDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return mNavDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return mNavDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
            		mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
         
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
         
        imgIcon.setImageResource(mNavDrawerItems.get(position).getIcon());        
        txtTitle.setText(mNavDrawerItems.get(position).getTitle());
        
        if(mNavDrawerItems.get(position).getCounterVisibility()){
        	txtCount.setText(mNavDrawerItems.get(position).getCount());
        }else{
        	txtCount.setVisibility(View.GONE);
        }
        
        return convertView;
	}
}
