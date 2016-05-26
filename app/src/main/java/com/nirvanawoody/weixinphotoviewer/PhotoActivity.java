package com.nirvanawoody.weixinphotoviewer;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

/**
 * Created by Admin on 2016/5/12.
 */
public class PhotoActivity extends FragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener{

	private PhotoViewPager viewPager;
	private String imgUrls[];
	private ArrayList<Rect> rects;
	private ArrayList<PhotoFragment> fragments;
	private int index;
	private PhotoFragmentAdapter adapter;
	private ViewPagerIndicator indicator;
	private View root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_photo);
		root = findViewById(R.id.root);
		viewPager = (PhotoViewPager)findViewById(R.id.viewpager);
		indicator = (ViewPagerIndicator)findViewById(R.id.indicator);
		imgUrls = getIntent().getStringArrayExtra("imgUrls");
		rects = getIntent().getParcelableArrayListExtra("bounds");
		index = getIntent().getIntExtra("index",0);
		if(rects == null || imgUrls == null){
			finish();
		}else {
			adapter = new PhotoFragmentAdapter(getSupportFragmentManager());
			fragments = new ArrayList<>();
			int rectSize = rects.size();
			for(int i =0;i<imgUrls.length;i++){
				PhotoFragment fragment = new PhotoFragment();
				Bundle bundle = new Bundle();
				bundle.putString("img", imgUrls[i]);
				if(i < rectSize){
					bundle.putParcelable("startBounds",rects.get(i));
				}
				fragment.setArguments(bundle);
				fragments.add(fragment);
			}
			viewPager.setAdapter(adapter);
			viewPager.getViewTreeObserver().addOnGlobalLayoutListener(this);
			indicator.setViewPager(viewPager);
			indicator.refreshIndicator(fragments.size());
			viewPager.setCurrentItem(index);
		}
	}


	@Override
	public void onGlobalLayout() {
		PhotoFragment fragment = fragments.get(index);
		fragment.transformIn(new SmoothImageView.onTransformListener() {

			@Override
			public void onTransformCompleted(SmoothImageView.Status status) {
				root.setBackgroundColor(Color.BLACK);
			}
		});
		viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	private class PhotoFragmentAdapter extends FragmentPagerAdapter {

		public PhotoFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			PhotoFragment fragment = fragments.get(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return fragments == null ? 0 : fragments.size();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			transformOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void transformOut(){
		int currentIndex = viewPager.getCurrentItem();
		if(currentIndex < rects.size()){
			PhotoFragment fragment = fragments.get(currentIndex);
			indicator.setVisibility(View.GONE);
			root.setBackgroundColor(Color.TRANSPARENT);
			fragment.transformOut(new SmoothImageView.onTransformListener() {
				@Override
				public void onTransformCompleted(SmoothImageView.Status status) {
					finish();
					PhotoActivity.this.overridePendingTransition(0, 0);
				}
			});
		}else {
			finish();
			this.overridePendingTransition(0,0);
		}
	}
}
