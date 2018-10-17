package com.guomao.propertyservice.widget.viewimage;

import com.guomao.propertyservice.widget.viewimage.PhotoViewAttacher.OnViewTapListener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

	private String[] imageUrls;
	private OnViewTapListener onViewTapListener;
	
	public ImagePagerAdapter(FragmentManager fm, String[] imageUrls, OnViewTapListener onViewTapListener) {
		super(fm);
		this.imageUrls = imageUrls;
		this.onViewTapListener = onViewTapListener;
	}

	@Override
	public int getCount() {
		return imageUrls!= null? imageUrls.length : 0;
	}

	@Override
	public Fragment getItem(int position) {
		return ViewImagesFragment.newInstance(imageUrls[position], onViewTapListener);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		final ViewImagesFragment fragment = (ViewImagesFragment) object;
		// As the item gets destroyed we try and cancel any existing work.
		fragment.cancelWork();
		super.destroyItem(container, position, object);
	}
}
