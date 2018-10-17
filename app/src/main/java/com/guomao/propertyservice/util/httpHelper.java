package com.guomao.propertyservice.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.main.MainApplication;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

/**
 * 
 * 项目暂时没用
 * http通讯类，提供了大文件下载的功能
 * @author Administrator
 *
 */
public class httpHelper {
	//static public String baseUrl = Const.BASE_URL;
	static public String PreUrl = MainApplication.finalUrl;
	static String TAG = "httpHealper";

	// 大文件下载，将url下载到SavePath/FileName里去
	static public void getFileData(Context context, String url,
			String SavePath, String FileName) {
		try {
			SharedPrefUtil.Log("getFileData url: " + url + " " + SavePath + "/"
					+ FileName);
			HttpClient client = new DefaultHttpClient();
			String requestUrl = CommenUrl.getActionUrl(CommenUrl.downloadImg)
					+ "/" + URLEncoder.encode(url, "utf-8");
			HttpGet httpGet = new HttpGet(requestUrl);
			SharedPrefUtil.Log("getFileData 1");
			// 执行
			HttpResponse ressponse = client.execute(httpGet);
			SharedPrefUtil.Log("getFileData 2");
			int code = ressponse.getStatusLine().getStatusCode();
			SharedPrefUtil.Log("getFileData 3" + code);

			if (code == HttpStatus.SC_OK) {
				SharedPrefUtil.Log("getFileData File:" + SavePath + "/"
						+ FileName);
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
					SharedPrefUtil
							.Log("SD card is not avaiable/writeable right now.");
					return;
				}
				File path = new File(SavePath);
				File file = new File(SavePath + "/" + FileName);
				if (!path.exists()) {
					SharedPrefUtil.Log("Create the path:" + SavePath);
					path.mkdirs();
				}
				if (!file.exists()) {
					SharedPrefUtil.Log("Create the file:" + FileName);
					file.createNewFile();
				}
				FileOutputStream output = new FileOutputStream(file);
				SharedPrefUtil.Log("getFileData FileStream OK");
				// 得到网络资源并写入文件
				InputStream input = ressponse.getEntity().getContent();
				SharedPrefUtil.Log("getFileData NetStream OK");
				byte b[] = new byte[1024];
				int j = 0;
				while ((j = input.read(b)) != -1) {
					output.write(b, 0, j);
				}
				SharedPrefUtil.Log("getFileData Write OK");
				output.flush();
				output.close();
			}
		} catch (Exception e) {

			SharedPrefUtil.Log(e.getMessage());
			// TODO: handle exception
		}
	}

	static public String PostWithFile(HashMap param, ArrayList<String> Files) {
		HttpClient client = new DefaultHttpClient();// 开启一个客户端 HTTP 请求
		HttpPost post = new HttpPost(PreUrl);// 创建 HTTP POST 请求
		MultipartEntity builder = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		StringBody stringBody = null;

		// builder.setCharset(Charset.forName("UTF-8"));//设置请求的编码格式
		//builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
		int count = 1;

		Set<Map.Entry<String, Object>> set = param.entrySet();
		for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it
				.hasNext();) {
			Map.Entry<String, Object> mapEnter = it.next();
			try {
				stringBody = new StringBody(mapEnter.getValue().toString(),
						Charset.forName(HTTP.UTF_8));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			//strentity = new StringBody(obj.toString(), Charset.forName(HTTP.UTF_8));
			
			builder.addPart(mapEnter.getKey(), stringBody);
			// builder.addTextBody( mapEnter.getKey(),
			// mapEnter.getValue().toString());

		}

		File file;
		for (String filename : Files) {
			file = new File(filename);
			// FileBody fileBody = new FileBody(file);//把文件转换成流对象FileBody
			// builder.addPart("file"+count, fileBody);
			//builder.addBinaryBody("Pic" + count, file);
			builder.addPart("Pic", new FileBody(file));
			count++;
		}

		post.setEntity(builder);// 设置请求参数
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "PostFile Error: " + e.getMessage());
			return "";
		}// 发起请求 并返回请求的响应

		if (response.getStatusLine().getStatusCode() == 200) {
			try {
				String ret = EntityUtils
						.toString(response.getEntity(), "utf-8");
				Log.i(TAG, "PostFile OK: " + ret);
				return ret;
			} catch (Exception e) {
				Log.i(TAG, "PostFile Error: " + e.getMessage());
				return "";
			}
		} else {
			Log.i(TAG, "PostFile Error: "
					+ response.getStatusLine().getReasonPhrase());
		}
		return "";
	}

	static public String Post(HashMap param, String url) {
		Log.d(TAG, "Post: " + url);
		String ret = "";
		// 和GET方式一样，先将参数放入List
		LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

		Iterator iter = param.entrySet().iterator();
		// System.out.println("----------->Out put LastInsertID");
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			params.add(new BasicNameValuePair(entry.getKey().toString(), entry
					.getValue().toString()));

			Log.d(TAG, "Post param " + entry.getKey().toString() + " : "
					+ entry.getValue().toString());
		}
		// params.add(new BasicNameValuePair("param2", "第二个参数"));
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost postMethod = new HttpPost(url);
			postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // 将参数填入POST
																				// Entity中
			Log.d(TAG, "Post Do");
			HttpResponse response = httpClient.execute(postMethod); // 执行POST方法
			Log.d(TAG, "Post DoOver");

			ret = EntityUtils.toString(response.getEntity(), "utf-8");
			Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); // 获取响应码
			Log.i(TAG, "result = " + ret); // 获取响应内容

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			ret = e.toString();
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			ret = e.toString();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ret = e.toString();
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ret = e.toString();
			e.printStackTrace();
		}
		Log.d(TAG, "Post ret: " + ret);
		return ret;
	}

}
