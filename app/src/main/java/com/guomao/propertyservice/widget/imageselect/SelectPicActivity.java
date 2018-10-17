package com.guomao.propertyservice.widget.imageselect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.FileProvider7JinMao;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.guomao.propertyservice.page.WebViewActivity;
import com.jinmaochina.propertyservice.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SelectPicActivity extends Activity{

	private static int SEL_CAPACITY = 1;

	public final static int FROM_DIALOG = 1;
	public final static int FROM_DYNAMIC = 2;

	public static final int REQUEST_ALBUM = 101;
	public static final int REQUEST_IMAGE = 102;

	private Context context = this;
	private ArrayList<String> dataList = new ArrayList<String>();
	private HashMap<String, ImageView> hashMap = new HashMap<String, ImageView>();
	private ArrayList<String> selectedDataList = new ArrayList<String>();
	private AlbumGridViewAdapter gridImageAdapter;
	private LinearLayout selectedImageLayout;
	private Button okButton;
	private HorizontalScrollView scrollview;
	private ImageLoader loader;

	private ImageView backView;

	public static  String mPhotoPath;
	private UserBiz userBiz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pic_activity);
		userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			selectedDataList = (ArrayList<String>) bundle.getSerializable("dataList");
			SEL_CAPACITY = bundle.getInt("SEL_CAPACITY",SEL_CAPACITY);
		}
		if(savedInstanceState != null){

			mPhotoPath = savedInstanceState.getString("path");
		}

		loader = ImageLoader.getInstance();
		init();
		initListener();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == WebViewActivity.START_CAMERA_REQUEST && resultCode == RESULT_OK) {

			if(selectedDataList == null)
				selectedDataList = new ArrayList<String>();

			if(selectedDataList.size() == SEL_CAPACITY)

				selectedDataList.set(SEL_CAPACITY-1, mPhotoPath);
			else
				selectedDataList.add(mPhotoPath);

			Intent finishintent = new Intent();
			finishintent.putExtra("type", REQUEST_ALBUM);
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("dataList", selectedDataList);
			finishintent.putExtras(bundle);
			setResult(RESULT_OK, finishintent);
			finish();

		}
		
	}
	private void udpdataView() {
		dataList.remove(0);
		List<String> temp = new ArrayList<String>(dataList);
		dataList.clear();
		dataList.add(0, "camera_default");
		dataList.add(1,mPhotoPath);
		dataList.addAll(temp);
		dataList.trimToSize();
		gridImageAdapter.notifyDataSetChanged();
		if(selectedDataList.size() >= SEL_CAPACITY){
			selectedDataList.set(selectedDataList.size()-1, mPhotoPath);
		}else{
			selectedDataList.add(mPhotoPath);
		}
		selectedDataList.trimToSize();
		initSelectImage();
	}

	private void scaleBitmap() {
		File pic  = new File(mPhotoPath);

		Options opts  = new Options();
		opts.inJustDecodeBounds= true;
		BitmapFactory.decodeFile(mPhotoPath, opts);
		int height=opts.outHeight;
		int width=opts.outWidth;

		int max = height >= width ? height:width;

		int sample = max/1024;
		opts.inSampleSize = sample>1?sample:1;
		opts.inJustDecodeBounds = false;

		Bitmap bm = BitmapFactory.decodeFile(mPhotoPath,opts);
		if(bm!=null){
			try {
				FileOutputStream fos = new FileOutputStream(pic);
				bm.compress(Bitmap.CompressFormat.JPEG, 20, fos);
			} catch (FileNotFoundException e) {
				L.printStackTrace(e);
			}finally{
				bm.recycle();
			}
		}
	}
	private void init() {
		//titleTextView.setText(R.string.select_images);

		GridView gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList, selectedDataList);
		gridView.setAdapter(gridImageAdapter);
		refreshData();
		selectedImageLayout = (LinearLayout) findViewById(R.id.selected_image_layout);
		okButton = (Button) findViewById(R.id.ok_button);
		backView = (ImageView) findViewById(R.id.leftView);
		scrollview = (HorizontalScrollView) findViewById(R.id.scrollview);

		initSelectImage();
	}

	private void initSelectImage() {
		if (selectedDataList == null)
			return;
		selectedImageLayout.removeAllViews();
		for (final String path : selectedDataList) {
			addSelectedImageView(path);
		}
		updateseledInfo();
	}

	private void addSelectedImageView(final String path) {
		ImageView imageView = (ImageView) LayoutInflater.from(SelectPicActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
		selectedImageLayout.addView(imageView);
		hashMap.put(path, imageView);
		Util.diaplayImage(loader, path, imageView, 51);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				removePath(path);
				gridImageAdapter.notifyDataSetChanged();
			}
		});
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onItemClick(final ToggleButton toggleButton, int position, final String path, boolean isChecked) {
				if(position == 0){

					SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String date = sDateFormat.format(new java.util.Date());
					try {
						if (StringUtil.isNull(userBiz.getUserId())) {
							mPhotoPath = DataFolder.getAppDataRoot() + "cache/images/"
									+ date + File.separator + System.currentTimeMillis()
									+ ".jpeg";
						} else {
							mPhotoPath = DataFolder.getAppDataRoot() + "cache/images/"
									+ date + File.separator + userBiz.getUserId() + "_"
									+ System.currentTimeMillis() + ".jpeg";
						}

						SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "CAMER_PHOTO_PATH", mPhotoPath);
						SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "ISTAKEPHOTO", "true");
						File mPhotoFile = new File(mPhotoPath);
						if (!mPhotoFile.getParentFile().exists()) {
							mPhotoFile.getParentFile().mkdirs();
						}
						if (!mPhotoFile.exists()) {
							mPhotoFile.createNewFile();
						}
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						//intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
						
						intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider7JinMao.getUriForFile(context, mPhotoFile));//将拍取的照片保存到指定URI
						intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
						startActivityForResult(intent,WebViewActivity.START_CAMERA_REQUEST);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					if (selectedDataList.size() >= SEL_CAPACITY) {
						toggleButton.setChecked(false);
						if (!removePath(path)) {
							Toast.makeText(SelectPicActivity.this, "只能选择" + SEL_CAPACITY + "张图片", Toast.LENGTH_SHORT).show();
						}
						return;
					}

					if (isChecked) {
						if (!hashMap.containsKey(path)) {
							ImageView imageView = (ImageView) LayoutInflater.from(SelectPicActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout, false);
							selectedImageLayout.addView(imageView);
							imageView.postDelayed(new Runnable() {

								@Override
								public void run() {

									int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();
									if (off > 0) {
										scrollview.smoothScrollTo(off, 0);
									}

								}
							}, 100);

							hashMap.put(path, imageView);
							selectedDataList.add(path);
							Util.diaplayImage(loader, path, imageView, 51);
							imageView.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									toggleButton.setChecked(false);
									removePath(path);
								}
							});
							updateseledInfo();
						}
					} else {
						removePath(path);
					}
				}
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.putExtra("type", REQUEST_ALBUM);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList", selectedDataList);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();

			}
		});


		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//onLeftViewClick(v);
				Intent intent = new Intent();
				intent.putExtra("type", REQUEST_ALBUM);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList", selectedDataList);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private boolean removePath(String path) {
		if (hashMap.containsKey(path)) {
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			removeOneData(selectedDataList, path);
			updateseledInfo();
			return true;
		} else {
			return false;
		}
	}

	private void removeOneData(ArrayList<String> arrayList, String s) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(s)) {
				arrayList.remove(i);
				arrayList.trimToSize();
				return;
			}
		}
	}

	private void refreshData() {

		new AsyncTask<Void, Void, ArrayList<String>>() {

			@Override
			protected void onPreExecute() {
				//progressBar.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			//
			@Override
			protected ArrayList<String> doInBackground(Void... params) {
				ArrayList<String> tmpList = new ArrayList<String>();
				Cursor cur = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media.DATA }, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " desc");
				if(cur == null){
					return tmpList;
				}
				cur.moveToFirst();
				while (!cur.isAfterLast()) {
					String path = cur.getString(cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
					tmpList.add(path);
					cur.moveToNext();
				}

				if (cur != null) {
					cur.close();
				}

				   cur = getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media.DATA }, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " desc");
				if(cur == null){
					return tmpList;
				}
				cur.moveToFirst();
				while (!cur.isAfterLast()) {
					String path = cur.getString(cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
					tmpList.add(path);
					cur.moveToNext();
				}
				if (cur != null) {
					cur.close();
				}
				return tmpList;
			}

			protected void onPostExecute(ArrayList<String> tmpList) {

				if (SelectPicActivity.this == null || SelectPicActivity.this.isFinishing()) {
					return;
				}
				//progressBar.setVisibility(View.GONE);
				dataList.clear();
				dataList.add(0, "camera_default");
				dataList.addAll(tmpList);
				dataList.trimToSize();
				gridImageAdapter.notifyDataSetChanged();
				return;

			}

        }.execute();
	}

	private void updateseledInfo(){
		//okButton.setText("完成(" + selectedDataList.size() + "/" + SEL_CAPACITY + ")");
		okButton.setText("完成");
	}

	public void allScan() { 
		sendBroadcast(new Intent( 
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
				Uri.parse("file://" + DataFolder.getAppDataRoot()))); 
	} 
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString("path",mPhotoPath);
		super.onRestoreInstanceState(savedInstanceState);
	}
}
