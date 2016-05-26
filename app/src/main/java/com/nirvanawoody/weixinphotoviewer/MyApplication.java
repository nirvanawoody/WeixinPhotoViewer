package com.nirvanawoody.weixinphotoviewer;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Admin on 2016/5/17.
 */
public class MyApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader();
	}


	private void initImageLoader() {


		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(false) // default
				.cacheOnDisk(true) // default
				.showImageOnLoading(R.mipmap.ic_default) // resource or
						// drawable
				.showImageForEmptyUri(R.mipmap.ic_default) // resource or
						// drawable
				.showImageOnFail(R.mipmap.ic_default).build();

		File imageCacheDir = StorageUtils.getCacheDirectory(this, true);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024)
				.diskCache(new UnlimitedDiskCache(imageCacheDir))
						// 自定义缓存路径
				.defaultDisplayImageOptions(options)
				.imageDownloader(
						new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // 超时时间
				.build();// 开始构建
		// 全局初始化此配置
		ImageLoader.getInstance().init(config);
	}
}
