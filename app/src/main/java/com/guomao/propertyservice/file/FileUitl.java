package com.guomao.propertyservice.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.L;

import android.annotation.SuppressLint;

public class FileUitl {

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 设置日期格式

	/**
	 * 写文件
	 * 
	 * @param path
	 * @param data
	 * @param append
	 *            没有判断 true为追加写入，false为覆盖写入，默认为false
	 */
	public static void file_write(String path, String data, boolean append) {
		FileOutputStream fout = null;
		try {
			if (path.contains(File.separator)) {
				File file = new File(DataFolder.getAppDataRoot() + path);
				if (file != null) {
					if (!file.exists()) {
						File pF = file.getParentFile();
						if (pF.exists()) {
							pF.mkdirs();
						}
						if (!file.exists()) {
							file.createNewFile();
						}
					}
				}
			}
			fout = new FileOutputStream(DataFolder.getAppDataRoot() + path,
					append);
			byte[] bytes = data.getBytes();
			fout.write(bytes);
			L.i("写入文件成功 路径：" + path);
		} catch (Exception e) {
			L.printStackTrace(e);
			L.i("写入文件失败");
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
				}
			}
		}

	}

	/**
	 * 读取文件 读取指定路径文件的完整内容，返回文件内容字符串
	 * 
	 * @param path
	 * @return
	 */

	public static String file_read(String path) {
		String res = "";
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(DataFolder.getAppDataRoot() + path);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");

		} catch (Exception e) {
			L.printStackTrace(e);
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}
		return res;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void delfile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File[] files = file.listFiles(); // 声明目录下所有的文件 files[];
				if (files == null || files.length == 0) {
					file.delete();
					return;
				}
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					delfile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
			L.i("文件删除成功！");
		} else {
			L.i("文件不存在！");
		}
	}

	/**
	 * 
	 * @param path
	 *            文件路径
	 */
	public static boolean file_exists(String path) {
		File file = new File(DataFolder.getAppDataRoot() + path);
		if (!file.exists()) {
			L.i("文件不存在！");
			return false;
		}
		L.i("文件存在！");
		return true;
	}

	/**
	 * 通过绝对路径判断
	 * 
	 * @param path
	 * @return
	 */
	public static boolean file_isExistsByAbsolutePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			L.i("文件不存在！");
			return false;
		}
		L.i("文件存在！");
		return true;
	}

	/**
	 * 创建目录文件
	 * 
	 * @param path
	 */
	public static void createPath(String path) {
		File file = new File(DataFolder.getAppDataRoot() + path);
		if (!file.exists()) {
			file.mkdirs();
			L.i("创建成功！");
		} else {
			L.i("创建失败！");
		}
	}

	@SuppressLint("SdCardPath")
	public static void writeReturnData(int page, String data) {
		File file = new File(DataFolder.getAppDataRoot()+"basedata.txt");
		FileWriter fw = null;
		BufferedWriter writer = null;
		try {
			fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			writer.write("TIME:" + df.format(new Date()));
			writer.write("\t" + page + "--->>>" + data + "\r\n");
			writer.newLine();// 换行
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
