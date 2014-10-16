package com.easytravel.easytravel.models;

public class NavDrawerItem {
	private String mTitle;
	private int mIcon;
	private String mCount = "0";
	private boolean mIsCounterVisible = false;
	
	public NavDrawerItem(){}

	public NavDrawerItem(String title, int icon){
		this.mTitle = title;
		this.mIcon = icon;
	}
	
	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
		this.mTitle = title;
		this.mIcon = icon;
		this.mIsCounterVisible = isCounterVisible;
		this.mCount = count;
	}
	
	public String getTitle(){
		return this.mTitle;
	}
	
	public int getIcon(){
		return this.mIcon;
	}
	
	public String getCount(){
		return this.mCount;
	}
	
	public boolean getCounterVisibility(){
		return this.mIsCounterVisible;
	}
	
	public void setTitle(String title){
		this.mTitle = title;
	}
	
	public void setIcon(int icon){
		this.mIcon = icon;
	}
	
	public void setCount(String count){
		this.mCount = count;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.mIsCounterVisible = isCounterVisible;
	}
}
