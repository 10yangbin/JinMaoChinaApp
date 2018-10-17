/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guomao.propertyservice.widget.viewimage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.widget.viewimage.PhotoViewAttacher.OnViewTapListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class ViewImagesActivity extends FragmentActivity implements OnViewTapListener {
	
	public static final int MORE_REQUEST = Activity.RESULT_CANCELED + 0xF1;
	public static final int REPORT_REQUEST = Activity.RESULT_CANCELED + 0xF2;
	
	public final static String UID = "uid";
	public final static String DID = "did";
	public final static String IMAGES = "images";
	public final static String SHOW_INDEX = "showIndex";

    private TextView pageTextView;
	
	private String[] images; 
	private int showIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 images = this.getIntent().getStringArrayExtra(IMAGES);
		 showIndex = this.getIntent().getIntExtra(SHOW_INDEX, 0); 				
		
		setContentView(R.layout.view_image_activity);

		initView();
	}
	
	public void onMoreButtonClick(View v){
		
	}
	
	private void initView(){
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), images, this);
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		pageTextView = (TextView) findViewById(R.id.pageTextView);
		mPager.setAdapter(mAdapter);
		 
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageSelected(int position) {
				Log.i("Icache", "onPageSelected = "+position);
				showIndex = position;	
				updatePageInfo();
			}
		});
	
		mPager.setCurrentItem(showIndex);
		updatePageInfo();
	}
	
	private void updatePageInfo(){
		pageTextView.setText(String.format("%d / %d", showIndex+1, images.length));
	}
	
	@Override
	public void onViewTap(View view, float x, float y) {
		// TODO Auto-generated method stub
		this.finish();
	}
	
	public boolean saveBitmap(Bitmap bitmap, String filePath){
		if(bitmap != null){
			 File file = new File(filePath);
	         if (!file.exists()) {	             
	         	try {	             	
               	 file.createNewFile();
           		 final OutputStream outStream = new FileOutputStream(file);
           	 
           		 bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
           		 outStream.flush();
           		 outStream.close();         
	            } catch (Exception e) {
	            	e.printStackTrace();
	               return false;
	            }
	         }         
		}		
		return true;
	}
	@Override
	public void finish() {
		setResult(RESULT_OK, null);
		super.finish();
	}
}
