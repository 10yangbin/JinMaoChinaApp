package com.guomao.propertyservice.widget.imageselect;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class Util {
	static DisplayImageOptions opts  = new DisplayImageOptions.Builder()    
			.imageScaleType(ImageScaleType.EXACTLY)  
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();  
	public static  void diaplayImage(ImageLoader loader,final String path,final ImageView imageView,float size) {
		//size =  DensityUtil.dip2px(MainApplication.getInstance(), size);
		loader.displayImage("file://"+path, imageView ,opts,new SimpleImageLoadingListener(){
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				imageView.setImageBitmap(loadedImage);
				imageView.setBackgroundColor(Color.WHITE);
			}
        } );
	}
	public static  void diaplayImage(final BaseAdapter adapter,ImageLoader loader,final String path,final ImageView imageView,float size) {
		//size =  DensityUtil.dip2px(MainApplication.getInstance(), size);
		loader.displayImage("file://"+path, imageView ,opts,new SimpleImageLoadingListener(){
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				imageView.setBackgroundColor(Color.WHITE);
				//adapter.notifyDataSetChanged();
			}
        } );
	}
}
