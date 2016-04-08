package com.example.news;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.view.ViewPager;

public class NewsPagerAdapter extends PagerAdapter{

	private List<View> vessel=new ArrayList<View>();
	
	public NewsPagerAdapter(List<View> vessel){
		this.vessel=vessel;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		 ((ViewPager) container).removeView(vessel.get(position));  
		super.destroyItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		 ((ViewPager) container).addView(vessel.get(position),0);  
		return super.instantiateItem(container, position);
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return false;
	}
}



