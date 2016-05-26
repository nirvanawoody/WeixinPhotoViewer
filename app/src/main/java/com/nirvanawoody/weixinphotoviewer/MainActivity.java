package com.nirvanawoody.weixinphotoviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


	private ListView mListView;

	private List<List<String>> data;
	private boolean preventDoubleClick = false; //预防双击
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.listview);
		data = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			data.add(ImageFactory.createImageSource());
		}
		adapter = new MyAdapter(this);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		if (preventDoubleClick) {
			preventDoubleClick = false;
		}
		super.onResume();
	}


	private View.OnClickListener photoListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (preventDoubleClick) {
				return;
			}
			preventDoubleClick = true;
			int index = (int) v.getTag();
			ViewGroup parent = (ViewGroup) v.getParent();
			List<String> imageUrls = (List<String>) parent.getTag();

			int childCount = parent.getChildCount();
			ArrayList<Rect> rects = new ArrayList<>();
			for (int i = 0; i < childCount; i++) {
				Rect rect = new Rect();
				View child = parent.getChildAt(i);
				child.getGlobalVisibleRect(rect);
				rects.add(rect);
			}
			Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
			String imageArray[] = new String[imageUrls.size()];
			for(int i = 0;i<imageArray.length;i++){
				imageArray[i] = imageUrls.get(i);
			}
			intent.putExtra("imgUrls", imageArray);
			intent.putExtra("index", index);
			intent.putExtra("bounds", rects);
			startActivity(intent);
			overridePendingTransition(0, 0);
		}
	};


	private class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView.findViewById(R.id.tv_title);
				holder.container = (RelativeLayout) convertView.findViewById(R.id.container);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText("假装这是第" + (position + 1) + "条朋友圈");
			List<String> imageUrls = data.get(position);
			holder.container.setTag(imageUrls);
			for (int i = 0; i < holder.container.getChildCount(); i++) {
				ImageView iv = (ImageView) holder.container.getChildAt(i);
				ImageLoader.getInstance().displayImage(imageUrls.get(i), iv);
				iv.setOnClickListener(photoListener);
				iv.setTag(i);
			}

			return convertView;
		}
	}

	private static class ViewHolder {
		private TextView textView;
		private RelativeLayout container;
	}
}
