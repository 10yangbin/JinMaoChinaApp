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

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.widget.viewimage.PhotoViewAttacher.OnViewTapListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class ViewImagesFragment extends Fragment {
	private static final String IMAGE_DATA_EXTRA = "image";
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.cacheInMemory(false)
	.cacheOnDisk(true)
	.build();
	private String image;
	private PhotoView mImageView;
    private ImageLoader imageLoader;
	private OnViewTapListener onViewTapListener;
	
	public static ViewImagesFragment newInstance( String imageUrl, OnViewTapListener onViewTapListener) {
		final ViewImagesFragment f = new ViewImagesFragment();

		final Bundle args = new Bundle();
		args.putCharSequence(IMAGE_DATA_EXTRA, imageUrl);
		f.setArguments(args);
		
		f.onViewTapListener = onViewTapListener;
		
		return f;
	}

	public ViewImagesFragment() {
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		image = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.view_image_fragment,
				container, false);
		mImageView = (PhotoView) v.findViewById(R.id.imageView);
        ProgressBar mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		
		mImageView.setOnViewTapListener(onViewTapListener);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (ViewImagesActivity.class.isInstance(getActivity())) {
			imageLoader = ImageLoader.getInstance();
			if(image != null && !image.contains("://")){
				image = "file://"+image;
			}
			imageLoader.displayImage(image, mImageView, options);
		}
	}

	public void cancelWork() {
		imageLoader.cancelDisplayTask(mImageView);
		mImageView.setImageDrawable(null);
		mImageView = null;
	}
}
