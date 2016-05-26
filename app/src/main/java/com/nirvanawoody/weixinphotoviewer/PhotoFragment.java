package com.nirvanawoody.weixinphotoviewer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Woody on 2016/5/12.
 */
public class PhotoFragment extends Fragment {

	private SmoothImageView ivPhoto;
	private ProgressBar progressBar;
	private Rect startBounds;
	private Activity activity;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_photo,null);
		activity = getActivity();
		ivPhoto = (SmoothImageView)rootView.findViewById(R.id.iv_photo);
		ivPhoto.setMinimumScale(0.5f);
		ivPhoto.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				((PhotoActivity)activity).transformOut();
			}
		});
		progressBar = (ProgressBar)rootView.findViewById(R.id.progress);
		Bundle args = getArguments();
		if(args !=null && args.containsKey("img")){
			String imgUrl = args.getString("img");
			startBounds = args.getParcelable("startBounds");
			File file = DiskCacheUtils.findInCache(imgUrl, ImageLoader.getInstance().getDiskCache());
			final boolean showLoading = file == null || !file.exists();
			ivPhoto.setTransformEnabled(!showLoading);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.resetViewBeforeLoading(false)  // default
					.cacheOnDisk(true) // default
					.showImageOnLoading(null) // resource or drawable
					.showImageForEmptyUri(R.mipmap.ic_default) // resource or drawable
					.showImageOnFail(R.mipmap.ic_default)
					.build();
			ImageLoader.getInstance().displayImage(imgUrl, ivPhoto,options, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String s, View view) {
					if(showLoading){
						progressBar.setVisibility(View.VISIBLE);
					}else {
						progressBar.setVisibility(View.GONE);
					}
				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason) {
					progressBar.setVisibility(View.GONE);
					ivPhoto.setTransformEnabled(true);
				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap) {
					progressBar.setVisibility(View.GONE);
					ivPhoto.setTransformEnabled(true);
				}

				@Override
				public void onLoadingCancelled(String s, View view) {
					progressBar.setVisibility(View.GONE);
					ivPhoto.setTransformEnabled(true);
				}
			});
		}
		return rootView;
	}



	public void transformIn(SmoothImageView.onTransformListener listener){
		ivPhoto.transformIn(startBounds,listener);

	}

	public void transformOut(SmoothImageView.onTransformListener listener){
		ivPhoto.transformOut(startBounds,listener);
	}


}
