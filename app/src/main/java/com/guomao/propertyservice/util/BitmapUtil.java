package com.guomao.propertyservice.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import com.guomao.propertyservice.config.Const;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class BitmapUtil {
	private static BitmapFactory.Options opt;

	public static Bitmap compressImage(Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > size) {
			// 重置baos即清空baos
			baos.reset();
			// 每次都减少10
			options -= 10;
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);

		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		// 把ByteArrayInputStream数据生成图片
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	public static ByteArrayInputStream compressImage2ByteArrayInputStream(
			Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > size) {
			// 重置baos即清空baos
			baos.reset();
			// 每次都减少10
			options -= 10;
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return isBm;
	}

	public static ByteArrayInputStream compressImage2ByteArrayInputStream(
			String path, BitmapFactory.Options o, int size) {
		Bitmap image = BitmapFactory.decodeFile(path, o);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		// 循环判断如果压缩后图片是否大于指定大小,大于继续压缩
		while (baos.toByteArray().length / 1024 > size) {
			// 重置baos即清空baos
			baos.reset();
			// 每次都减少10
			options -= 5;
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		if (image != null) {
			image.recycle();
		}
		return isBm;
	}

	public synchronized static BitmapFactory.Options getDefaultOption() {
		if (opt == null) {
			opt = new Options();
			opt.inJustDecodeBounds = true;
		}
		return opt;
	}

	public static ByteArrayInputStream compressImage2ByteArrayInputStream(
			String path, int size) {
		BitmapFactory.Options opt = new Options();
		opt.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(path, opt);
		int simpleSize = computeSampleSize(opt, 320, Const.DEFAULT_PIC_SIZE);
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = simpleSize;
		Bitmap image = BitmapFactory.decodeFile(path, opt);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		// // 循环判断如果压缩后图片是否大于指定大小,大于继续压缩
		// while (baos.toByteArray().length / 1024 > size) {
		// // 重置baos即清空baos
		// baos.reset();
		// // 每次都减少10
		// options -= 10;
		// // 这里压缩options%，把压缩后的数据存放到baos中
		// image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		// }
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		if (image != null) {
			image.recycle();
		}
		return isBm;
	}

	public static byte[] compressImage2Byte(String path, int size) {
		BitmapFactory.Options newOpts = new Options();
		//opt.inJustDecodeBounds = true;

		//BitmapFactory.decodeFile(path, opt);
		//int simpleSize = computeSampleSize(opt, 480, Const.DEFAULT_PIC_SIZE);
		
		newOpts.inJustDecodeBounds = false;
		newOpts.inSampleSize = 4;
		
		newOpts.inPreferredConfig = Config.RGB_565;//该模式是默认的,可不设  
	    newOpts.inPurgeable = true;// 同时设置才会有效  
	    newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收  
	    
		Bitmap image = BitmapFactory.decodeFile(path, newOpts);
		if(image==null){
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 30, baos);
		byte[] bytes = baos.toByteArray();
		if (image != null) {
			image.recycle();
		}
		return bytes;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels * 1024);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static Bitmap imrez(Bitmap image, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap, size);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap getBitmapFromFile(File dst, int width, int height,
			int size) {
		if (null != dst && dst.exists()) {
			BitmapFactory.Options opts = null;
			if (width > 0 && height > 0) {
				opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(dst.getPath(), opts);
				// 计算图片缩放比例
				final int minSideLength = Math.min(width, height);
				opts.inSampleSize = computeSampleSize(opts, minSideLength,
						width * height);
				opts.inJustDecodeBounds = false;
				opts.inInputShareable = true;
				opts.inPurgeable = true;
			}
			try {
				return compressImage(
						BitmapFactory.decodeFile(dst.getPath(), opts), size);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
