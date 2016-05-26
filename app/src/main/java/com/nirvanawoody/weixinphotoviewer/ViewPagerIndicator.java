package com.nirvanawoody.weixinphotoviewer;

import android.content.Context;
import android.support.v4.view.MyViewPager;
import android.support.v4.view.MyViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class ViewPagerIndicator extends LinearLayout implements
		OnPageChangeListener {
	
	private MyViewPager mViewPager;
	private Context mContext;
	private int normalResId = R.drawable.page_indicator;
	private int seletedResId = R.drawable.page_indicator_focused;
	private int margin;
	private int count = 0;
	
	public interface OnIndicatorChangeListener{
		public void onPageScrollStateChanged(int arg0);
		public void onPageScrolled(int arg0, float arg1, int arg2);
		public void onPageSelected(int arg0);
	}
	
	private OnIndicatorChangeListener mListener;

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	private void init(){
		margin = 10;
		this.setOrientation(HORIZONTAL);
	}
	
	public void refreshIndicator(int count){
		this.removeAllViews();
		this.count = count;
		if(count > 1){
			for(int i = 0;i<count;i++){
				ImageView img = new ImageView(mContext);
				img.setImageResource(normalResId);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = margin;
				this.addView(img,params);
			}
			refreshViewByPosition(0);
			this.setVisibility(View.VISIBLE);
		}else {
			this.setVisibility(View.INVISIBLE);
		}
	}
	
	private void refreshViewByPosition(int position){
		int selectedPos = position % count;
		for(int i = 0;i<this.getChildCount();i++){
			ImageView imgView = (ImageView) this.getChildAt(i);
			if(i == selectedPos){
				imgView.setImageResource(seletedResId);
			}else {
				imgView.setImageResource(normalResId);
			}
		}
		
	}
	
	public MyViewPager getViewPager() {
		return mViewPager;
	}

	public void setViewPager(MyViewPager mViewPager) {
		this.mViewPager = mViewPager;
		this.mViewPager.setOnPageChangeListener(this);
	}

	public OnIndicatorChangeListener getOnIndicatorChangeListener() {
		return mListener;
	}

	public void setOnIndicatorChangeListener(OnIndicatorChangeListener mListener) {
		this.mListener = mListener;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(mListener !=null){
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if(mListener !=null){
			mListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		if(mListener !=null){
			mListener.onPageSelected(arg0);
		}
		refreshViewByPosition(arg0);
	}

}
