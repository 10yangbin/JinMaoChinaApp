package com.guomao.propertyservice.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class FileUtil {

	public static String readFromRaw(Context context, int fileInRaw) {
		String res = "";
		try {
			InputStream in = context.getResources().openRawResource(fileInRaw);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "GBK");
			// res = new String(buffer,"GBK");
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static void createFile(String path) {
		Bitmap bitmap = BitmapFactory.decodeFile(path, getBitmapOption(2)); // 将图片的长和宽缩小味原来的1/2
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);
			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) {
				new FileOutputStream(new File(path)).write(photoBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void createFile(String path, Bitmap bitmap) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] photoBytes = baos.toByteArray();
			// tag.str_base64 = new String(Base64Coder.encodeLines(photoBytes));
			if (photoBytes != null) {
				new FileOutputStream(new File(path)).write(photoBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Options getBitmapOption(int inSampleSize) {
		System.gc();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inSampleSize = inSampleSize;
		return options;
	}

	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public static int upZipFile(File zipFile, String site_id, String folderPath)
			throws IOException {
		// public static void upZipFile() throws Exception{
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d("upZipFile", "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			OutputStream os = null;
			try {
				String realName = null;
				if (ze.getName().endsWith("base.db")) {
					realName = site_id + "base.db";
				} else if ("business.db".equalsIgnoreCase(ze.getName())) {
					realName = site_id + "business.db";
				}
				os = new BufferedOutputStream(new FileOutputStream(
						getRealFileName(folderPath, realName)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		return 0;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 0) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			return ret;
		}
		return ret;
	}
}
