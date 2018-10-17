package com.guomao.propertyservice.widget.imageselect;

import java.util.ArrayList;

import com.jinmaochina.propertyservice.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class AlbumGridViewAdapter extends BaseAdapter implements
OnClickListener {

	private Context mContext;
	private ArrayList<String> dataList;
	private ArrayList<String> selectedDataList;
	private DisplayMetrics dm;
	private ImageLoader loader;

	public AlbumGridViewAdapter(Context c, ArrayList<String> dataList,
			ArrayList<String> selectedDataList) {

		mContext = c;
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;

		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
		.getMetrics(dm);
		loader = ImageLoader.getInstance();

	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 存放列表项控件句柄
	 */
	private class ViewHolder {
		public ImageView imageView;
		public ToggleButton toggleButton;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image_view);
			viewHolder.toggleButton = (ToggleButton) convertView
					.findViewById(R.id.toggle_button);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position);
		else
			path = "camera_default";
		viewHolder.imageView.getTag();

		viewHolder.imageView.setTag(path);
		//	loader.displayImage(path, viewHolder.imageView);
		loader.cancelDisplayTask(viewHolder.imageView);
		if (path.contains("camera_default")) {
			viewHolder.imageView.setImageResource(R.drawable.ablum_camera);
		} else {
			Util.diaplayImage(this,loader, path, viewHolder.imageView, 88);
		}
		viewHolder.toggleButton.setTag(position);
		viewHolder.toggleButton.setOnClickListener(this);
		if (isInSelectedDataList(path)) {
			viewHolder.toggleButton.setChecked(true);
		} else {
			viewHolder.toggleButton.setChecked(false);
		}

		return convertView;
	}

	private boolean isInSelectedDataList(String selectedString) {
		if(selectedDataList == null || selectedDataList.isEmpty()) return false;
		for (int i = 0; i < selectedDataList.size(); i++) {
			if (selectedDataList.get(i).equals(selectedString)) {
				return true;
			}
		}
		return false;
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}

	@Override
	public void onClick(View view) {
		if (view instanceof ToggleButton) {
			ToggleButton toggleButton = (ToggleButton) view;
			int position = (Integer) toggleButton.getTag();
			if (dataList != null && mOnItemClickListener != null
					&& position < dataList.size()) {
				mOnItemClickListener.onItemClick(toggleButton, position,
						dataList.get(position), toggleButton.isChecked());
			}
		}
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		void onItemClick(ToggleButton view, int position, String path,
                         boolean isChecked);
	}

}
