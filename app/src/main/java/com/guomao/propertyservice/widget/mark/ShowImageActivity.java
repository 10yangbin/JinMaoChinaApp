package com.guomao.propertyservice.widget.mark;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap.Config;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.jinmaochina.propertyservice.R;
/**
 * 
 * @author yang.liu
 *查看标记过的图片的activity
 */
public class ShowImageActivity extends Activity {
    private File file;
	private float DownX,DownY,moveX,moveY;
	private long currentMS;
	private int dpi;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_show_image);
    String path = getIntent().getStringExtra("path");
    ZoomImageView zoomimage = (ZoomImageView) findViewById(R.id.zoomimage);
	if(path ==null){
		Toast.makeText(this, "无法获取该文件", Toast.LENGTH_SHORT).show();
		return;
	}
	else if(path.startsWith("file://")){
		path = path.substring("file://".length());
	}
	try {
		zoomimage.setImageBitmap(compressImageFromFile(path).copy(Config.ARGB_8888, true));
	} catch (Exception e) {
		Toast.makeText(this, "图片下载失败,请检查网络是否流畅。", Toast.LENGTH_SHORT).show();
		try {
			File file = new File(path);
			file.delete();
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	zoomimage.isDoubleClick(false);
	//file=new File(path);
}
private Bitmap compressImageFromFile(String srcPath) {  
    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
    newOpts.inJustDecodeBounds = true;//只读边,不读内容  
    Bitmap bitmap=BitmapFactory.decodeFile(srcPath,newOpts);
    newOpts.inJustDecodeBounds = false;  
    int w = newOpts.outWidth;  
    int h = newOpts.outHeight;  
    int max = Math.max(w, h);
    /*if(dpi>320){
    	 newOpts.inSampleSize = 2;
    }else{
    	 newOpts.inSampleSize = 4;
    }*/
    newOpts.inSampleSize = 2;
   //设置采样率  
    newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设  
    newOpts.inPurgeable = true;// 同时设置才会有效  
    newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
    bitmap=BitmapFactory.decodeFile(srcPath,newOpts);
    return bitmap;  
}   
private int getDpi() {
	return getResources().getDisplayMetrics().densityDpi;
}
}
