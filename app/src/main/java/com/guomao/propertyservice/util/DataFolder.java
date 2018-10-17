package com.guomao.propertyservice.util;

import java.io.File;

import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.main.MainApplication;

import android.os.Environment;

public class DataFolder {
	private static String appDataRoot = null;

	public static String getAppDataRoot() {
		if (appDataRoot == null) {
			CreateDataFolders();
		}
		return appDataRoot;
	}

	@SuppressWarnings("static-access")
	private static void CreateDataFolders() {
		String gen = null;
		String formalUrl = "http://101.200.34.128:8090/appserver";
		if (MainApplication.getInstance().finalUrl.equals(formalUrl)) {
			gen = "JinMao_Realse";
		} else {
			gen = "JinMao_Test";
		}
		File files = new File(Environment.getExternalStorageDirectory()
				+ File.separator + gen);
		if (!files.exists()) {
			files.mkdirs();
		}
		
		//删除之前正式环境，用户手机里面无效的log日志文件
		if(!Const.DEBUG){
			if(files.exists()){
				File[] fl = files.listFiles(); 
				 for (int i=0; i<fl.length; i++)  
	             {  
	                 if(fl[i].toString().endsWith(".info"))//|| fl[i].toString().endsWith(".txt")  
	                 {  
	                    fl[i].delete();
	                    System.out.println(fl[i].getName());
	                 }  
	             }    
			}
		}
		
		File files2 = new File(Environment.getExternalStorageDirectory()
				+ File.separator + gen + "/db");
		if (!files2.exists()) {
			files2.mkdirs();
		}
		if (!(files.canWrite() && files.canRead())) {
			files = MainApplication.getInstance().getFilesDir();
			if (!(files.canWrite() && files.canRead())) {
				files = MainApplication.getInstance().getExternalCacheDir();
			}
		}
		appDataRoot = files.getAbsolutePath() + File.separator;

	}
}
