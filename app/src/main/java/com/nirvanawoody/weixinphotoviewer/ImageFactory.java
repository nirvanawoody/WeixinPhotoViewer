package com.nirvanawoody.weixinphotoviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Woody on 2016/5/17.
 */
public class ImageFactory {
	private static List<String> urls = new ArrayList<>();

	static {
		urls.add("http://p1.image.hiapk.com/uploads/allimg/131015/23-131015152128.jpg");
		urls.add("http://p1.image.hiapk.com/uploads/allimg/131015/23-131015152131.jpg");
		urls.add("http://p3.image.hiapk.com/uploads/allimg/131015/23-131015152132.jpg");
		urls.add("http://www.deskcar.com/desktop/fengjing/200895150214/21.jpg");
		urls.add("http://image.tuwang.com/Nature/FengGuang-1600-1200/FengGuang_pic_abx@DaTuKu.org.jpg");
		urls.add("http://img.jsqq.net/uploads/allimg/141128/1_141128005459_1.jpg");
//		urls.add("http://5.26923.com/download/pic/000/245/718dfc8322abe39627591e4a495767af.jpg");
//		urls.add("http://image.tianjimedia.com/uploadImages/2011/286/8X76S7XD89VU.jpg");
//		urls.add("http://www.bz55.com/uploads/allimg/141218/140-14121PT942-50.jpg");
		urls.add("http://bcs.91.com/wisedown/img/0/480_800/e1a02dacd560dedbbcd8caf20e99d551.jpg");
		urls.add("http://img0.imgtn.bdimg.com/it/u=1186783204,2695593404&fm=21&gp=0.jpg");
		urls.add("http://img0.imgtn.bdimg.com/it/u=3641914969,2162295909&fm=21&gp=0.jpg");
	}

	public static List<String> createImageSource(){
		List<String> source = new ArrayList<>(urls);
		List<String> data = new ArrayList<>();
		Random random = new Random();
		int size = source.size();
		for(int i = 0;i < size;i++){
			int index = random.nextInt(source.size());
			data.add(source.get(index));
			source.remove(index);
		}
		return  data;
	}
}
