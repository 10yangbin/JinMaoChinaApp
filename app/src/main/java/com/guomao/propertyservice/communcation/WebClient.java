package com.guomao.propertyservice.communcation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.util.FunctionUtil;
import com.guomao.propertyservice.util.L;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;


/**
 * WebClient,联网发送请求的封装
 * 整个项目用很处很多
 * @author Administrator
 *
 */
public class WebClient{

	private DefaultHttpClient httpClient;
	private static final String TAG = "WebClient";
	private static final int RE_TRY_COUNT = 5;
	private int count = 0;
	private static WebClient instance = null;
	private UserBiz userBiz;
	
	private CookieStore myCookieStore;

	/**
	 * 设置user的cookie
	 */
	public void useCookie(){
		httpClient.setCookieStore(myCookieStore);
	}
	/**
	 * 保存cookie
	 */
	public void saveCookie() {
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();  
		for (Cookie cookie:cookies){  
			myCookieStore.addCookie(cookie);  
		}
	}

	/**
	 * 保存cookie
	 */
	public void saveCookie(DefaultHttpClient httpClient) {
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();  
		for (Cookie cookie:cookies){  
			myCookieStore.addCookie(cookie);  
		}
	}
	public List<Cookie> getCookie(){
		return myCookieStore.getCookies(); 
	}
	public CookieStore getCookieStore(){
		return myCookieStore;
	}


	private WebClient() {
		
		SSLSocketFactory sf=null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClientParams.setRedirecting(params, false);

		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https",sf , 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schemeRegistry);

		HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 3) {
					return false;
				}
				if (exception instanceof org.apache.http.NoHttpResponseException) {
					L.i(TAG, "retry times:" + executionCount);
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					return false;
				}
				HttpRequest request = (HttpRequest) context
						.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                return idempotent;
            }
		};
		httpClient = new DefaultHttpClient(manager, params);
		myCookieStore = MainApplication.getInstance().getCookieStore();
		httpClient.setHttpRequestRetryHandler(retryHandler);
	}
	
	class SSLSocketFactoryEx extends SSLSocketFactory {
				
		    SSLContext sslContext = SSLContext.getInstance("TLS"); 
		
		    public SSLSocketFactoryEx(KeyStore truststore)
		
		            throws NoSuchAlgorithmException, KeyManagementException,
		
		            KeyStoreException, UnrecoverableKeyException {
		
		        super(truststore); 
		
		        TrustManager tm = new X509TrustManager() {	 
		
		            @Override
		
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		
		                return null;
		
		            }	 
		
		            @Override
		
		            public void checkClientTrusted(
		
		                    java.security.cert.X509Certificate[] chain, String authType)

                    {
		
		            }
		
		
		            @Override
		
		            public void checkServerTrusted(
		
		                    java.security.cert.X509Certificate[] chain, String authType)

                    {
	
		            }
		
		        };
		
		    
		
		        sslContext.init(null, new TrustManager[] { tm }, null);
		
		    }
	}
	
	
	public synchronized static WebClient getInstance(){
		if(instance == null){
			instance = new WebClient();
		}
		return instance;
	}
	public Object clone() throws CloneNotSupportedException {
	    throw new CloneNotSupportedException();
	}
	/*public String doGet(String url) throws Exception {
		String strResp = "";
		HttpGet get = new HttpGet(url);
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}
		get.addHeader("site_id", site_id);
		get.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		
		L.v(TAG, "HttpGet URL" + url);
		long currentTimeMillis = System.currentTimeMillis();
		try {
			L.v(TAG, "doGet:" + url);
			HttpResponse resp = httpClient.execute(get);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(resp.getEntity());
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			throw (new Exception());
		} finally {
			get.abort();
		}

		L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i(TAG, "response:" + strResp);

		return GetResultFromJsonRespons(strResp);

	}*/

	/*public String doPost(String url) throws Exception {

		String strResp = "";
		HttpPost post = new HttpPost(url);
		L.v(TAG, "HTTP Post: " + url);
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}
		post.addHeader("site_id", site_id);
		post.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));

		long currentTimeMillis = System.currentTimeMillis();
		try {

			HttpResponse resp = httpClient.execute(post);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(resp.getEntity());
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			L.printStackTrace(e);
			throw (e);
		} finally {
			post.abort();
		}

		L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i(TAG, "response:" + strResp);

		return GetResultFromJsonRespons(strResp);
	}*/

	/*public String doJasonPostWithFile(String url, JSONObject obj,
			List<String> files) throws Exception {
		count++;

		StringBody strentity;
		try {
			obj.put("platform", "android");
		} catch (JSONException e2) {
			e2.printStackTrace();
		}

	//	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		MultipartEntity builder=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		//builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式

		strentity = new StringBody(obj.toString(), Charset.forName(HTTP.UTF_8));
		//strentity=new StringBody(text, Ch)
		builder.addPart("data", strentity);

		int fCount = 1;
		File file;
		if (files != null) {
			for (String filename : files) {
				try {
					file = new File(filename);
					//builder.addBinaryBody("file" + fCount, file);
					builder.addPart("file" + fCount, new FileBody(file));
					fCount++;
				} catch (Exception e) {
				}
			}
		}

		String strResp = "";
		HttpPost httpPost = new HttpPost(url);
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		httpPost.addHeader("site_id", site_id);
		httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		L.v(TAG, "HTTP Post: " + url);
		L.v(TAG, "HTTP Post: " + obj.toString());
		long currentTimeMillis = System.currentTimeMillis();

		try {
			useCookie();
			httpPost.setEntity(builder);
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
				saveCookie();
			} else {
				throw (new Exception("服务器响应异常"));
			}
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains("EPIPE")) {
				count++;
				if (count > RE_TRY_COUNT) {
					count = 0;
					throw (e);
				}
				httpPost.abort();
				return doJasonPostWithFile(url, obj, files);
			} else {
				count = 0;
				httpPost.abort();
				if (e instanceof IOException) {
					throw new Exception("您的网络不给力~");
				} else {
					throw (e);
				}
			}
		} finally {
			count = 0;
			httpPost.abort();
		}

		L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i(TAG, "response:" + strResp);

		return GetResultFromJsonRespons(strResp);
	}*/

	public String doJsonPost(String url, JSONObject obj) throws Exception {
		count++;
		StringEntity strentity;
		try {
			obj.put("platform", "android");
		} catch (JSONException e2) {
			L.printStackTrace(e2);
		}
		try {
			strentity = new StringEntity(obj.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			L.printStackTrace(e1);
			throw (new Exception("编码错误"));
		}

		String strResp = "";
		// HttpPost httpPost = new
		// HttpPost("http://192.168.0.46:8088/LongforFM/testAdd");
		HttpPost httpPost = new HttpPost(url);
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		httpPost.addHeader("site_id", site_id);
		httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		L.v(TAG, "HTTP Post: " + url);
		L.v(TAG, "HTTP Post: " + obj.toString());
		long currentTimeMillis = System.currentTimeMillis();

		try {
			useCookie();
			httpPost.setEntity(strentity);
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
				saveCookie();
			} else {
				throw (new IOException("服务器响应失败"));
			}
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains("EPIPE")) {
				count++;
				if (count > RE_TRY_COUNT) {
					count = 0;
					throw (new Exception());
				}
				httpPost.abort();
				return doJsonPost(url, obj);
			} else {
				count = 0;
				httpPost.abort();
				if (e instanceof IOException) {
					throw new Exception("您的网络不给力~");
				} else {
					throw (e);
				}
			}
		} finally {
			count = 0;
			httpPost.abort();
		}

		L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i(TAG, "response:" + strResp);

		return GetResultFromJsonRespons(strResp);
	}

	public String doJasonPost(String url, JSONObject obj,boolean tag) throws Exception {
		if(tag){
			count++;
			StringEntity strentity;
			try {
				obj.put("platform", "android");
			} catch (JSONException e2) {
				L.printStackTrace(e2);
			}
			try {
				strentity = new StringEntity(obj.toString(), HTTP.UTF_8);
			} catch (UnsupportedEncodingException e1) {
				L.printStackTrace(e1);
				throw (new Exception("编码错误"));
			}

			String strResp = "";
			// HttpPost httpPost = new
			// HttpPost("http://192.168.0.46:8088/LongforFM/testAdd");
			HttpPost httpPost = new HttpPost(url);
			if(userBiz == null)
				userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
			User currentUser = userBiz.getCurrentUser();
			String site_id = "0";
			if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
				site_id = currentUser.getSite_id();
			}
			else{
				site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
				if(StringUtil.isNull(site_id)){
					site_id = "0";
				}
			}
			httpPost.addHeader("site_id", site_id);
			httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
			L.v(TAG, "HTTP Post: " + url);
			L.v(TAG, "HTTP Post: " + obj.toString());
			long currentTimeMillis = System.currentTimeMillis();

			try {
				useCookie();
				httpPost.setEntity(strentity);
				HttpResponse resp = httpClient.execute(httpPost);
				if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
					strResp = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
					saveCookie();
				} else {
					throw (new IOException("服务器响应失败"));
				}
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains("EPIPE")) {
					count++;
					if (count > RE_TRY_COUNT) {
						count = 0;
						throw (e);
					}
					httpPost.abort();
					return doJasonPost(url, obj,tag);
				} else {
					count = 0;
					httpPost.abort();
					throw (e);
				}
			} finally {
				count = 0;
				httpPost.abort();
			}

			L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
			L.i(TAG, "response:" + strResp);

			return strResp;
		}else{
			return doJsonPost(url, obj);
		}

	}

/*	public String doJasonPostWithFile(String url, JSONObject obj,
			List<String> files,boolean tag) throws Exception {

		if(tag){
			count++;
			StringBody strentity;
			try {
				obj.put("platform", "android");
			} catch (JSONException e2) {
				e2.printStackTrace();
			}

			MultipartEntity builder = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			//builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式

			strentity = new StringBody(obj.toString(), Charset.forName(HTTP.UTF_8));
			builder.addPart("data", strentity);

			int fCount = 1;
			File file;
			if (files != null) {
				for (String filename : files) {
					try {
						file = new File(filename);
						builder.addPart("file" + fCount, new FileBody(file));
						fCount++;
					} catch (Exception e) {
					}
				}
			}

			String strResp = "";
			HttpPost httpPost = new HttpPost(url);
			if(userBiz == null)
				userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
			User currentUser = userBiz.getCurrentUser();
			String site_id = "0";
			if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
				site_id = currentUser.getSite_id();
			}else{
				site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
				if(StringUtil.isNull(site_id)){
					site_id = "0";
				}
			}
			httpPost.addHeader("site_id", site_id);
			httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
			L.v(TAG, "HTTP Post: " + url);
			L.v(TAG, "HTTP Post: " + obj.toString());
			long currentTimeMillis = System.currentTimeMillis();

			try {
				useCookie();
				httpPost.setEntity(builder);
				HttpResponse resp = httpClient.execute(httpPost);
				if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
					strResp = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
					saveCookie();
				} else {
					throw (new Exception("服务器响应异常"));
				}
			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().contains("EPIPE")) {
					count++;
					if (count > RE_TRY_COUNT) {
						count = 0;
						throw (e);
					}
					httpPost.abort();
					return doJasonPostWithFile(url, obj, files);
				} else {
					count = 0;
					httpPost.abort();
					throw e;
				}
			} finally {
				count = 0;
				httpPost.abort();
			}

			L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
			L.i(TAG, "response:" + strResp);

			return strResp;
		}else{
			return doJasonPostWithFile(url, obj, files);
		}
	}*/





	public String doJasonPost(String url, JSONObject obj, byte[] data)
			throws Exception {

		int jsonLength = obj.toString().getBytes().length;
		byte[] postData = new byte[4 + jsonLength + data.length];
		System.arraycopy(intToByteArray(jsonLength), 0, postData, 0, 4);
		System.arraycopy(obj.toString().getBytes(), 0, postData, 4, jsonLength);
		System.arraycopy(data, 0, postData, 4 + jsonLength, data.length);

		String strResp = "";
		HttpPost httpPost = new HttpPost(url);
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		httpPost.addHeader("site_id", site_id);
		httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		L.v(TAG, "HTTP Post: " + url);
		L.v(TAG, "HTTP Post: " + obj.toString());
		long currentTimeMillis = System.currentTimeMillis();

		if (data != null) {
			httpPost.setEntity(new ByteArrayEntity(postData));
		}
		try {
			useCookie();
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(resp.getEntity());
				saveCookie();
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			throw (new Exception());
		} finally {
			httpPost.abort();
		}

		L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i(TAG, "response:" + strResp);

		return GetResultFromJsonRespons(strResp);
	}

	public byte[] DownloadByteData(String url, JSONObject obj) throws Exception {
		byte[] data = null;
		StringEntity strentity;
		try {
			strentity = new StringEntity(obj.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			throw (new Exception());
		}

		HttpPost httpPost = new HttpPost(url);
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		httpPost.addHeader("site_id", site_id);
		httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		try {
			useCookie();
			httpPost.setEntity(strentity);
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				data = EntityUtils.toByteArray(resp.getEntity());
				saveCookie();
			} else {
				throw (new Exception());
			}
			return data;
		} catch (Exception e) {
			throw (new Exception());
		} finally {
			httpPost.abort();
		}

	}

	private String GetResultFromJsonRespons(String jsonRepons) throws Exception {

		JSONObject jsonData;
		try {
			jsonData = new JSONObject(jsonRepons);
			if ("SUCCESS".equalsIgnoreCase(jsonData.getString("Status"))) {
				return jsonData.optString("UserObj");
			} else {
				throw (new Exception(jsonData.optString("Message")));
			}
		} catch (JSONException e) {
			throw (new Exception("数据格式异常"));
		}
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	//
	// class CountingMultiPartEntity extends StringEntity{
	// private OutputStream lastOutputStream_;
	// private CountingOutputStream outputStream_;
	// private UploadProgressListener listener_;
	//
	// public CountingMultiPartEntity(String s,UploadProgressListener listener)
	// throws UnsupportedEncodingException {
	// super(s);
	// this.listener_ = listener;
	// }
	//
	// public CountingMultiPartEntity(String s, String
	// charset,UploadProgressListener listener)
	// throws UnsupportedEncodingException {
	// super(s, charset);
	// this.listener_ = listener;
	// }
	// @Override
	// public void writeTo(OutputStream outstream) throws IOException {
	// if(lastOutputStream_ == null|| (lastOutputStream_ != outstream)){
	// lastOutputStream_ = outstream;
	// outputStream_ = new CountingOutputStream(outstream);
	// }
	// super.writeTo(outputStream_);
	// }
	//
	// private class CountingOutputStream extends BufferedOutputStream {
	// private long transferred = 0;
	// private OutputStream wrappedOutputStream_;
	//
	// public CountingOutputStream(OutputStream out) {
	// super(out);
	// wrappedOutputStream_ = out;
	// }
	//
	// @Override
	// public void write(byte[] buffer, int offset, int count)
	// throws IOException {
	// wrappedOutputStream_.write(buffer,offset,count);
	// transferred += count;
	// Log.i("INFO","COUNT:"+count+"TRANSFERRED:"+transferred);
	// super.write(buffer, offset, count);
	// if(listener_ !=null){
	// listener_ .onProgress(transferred);
	// }
	// }
	//
	// @Override
	// public void write(int oneByte) throws IOException {
	// super.write(oneByte);
	// }
	//
	// }
	// }
	// public interface UploadProgressListener{
	// public void onProgress(long send);
	// }
	public String doPost(String url,List<NameValuePair> pairs)throws Exception{

		return doPost(url, pairs, null);
	}

	public String doPost(String url,List<NameValuePair> pairs,List<String>files) throws Exception{
		count++;
		StringBody strentity;
		String strResp = ""; 
		if(userBiz == null)
			userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		try {
			pairs.add(new BasicNameValuePair("platform", "android"));
			User currentUser = userBiz.getCurrentUser();
			//			if(!url.contains(CommenUrl.loginAction)||!url.contains(CommenUrl.logOutAction)){
			//				if (currentUser.getUser_id() == "" || currentUser.getSite_id() == "") {
			//					Intent intent = new Intent(Const.ACTION_NetworkImpl_USER_NULL);
			//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//					MainApplication.getInstance().sendBroadcast(intent);
			//				}
			//			}
			if(currentUser!=null){
				//				if(StringUtil.isNull(currentUser.getUser_id())){//yon
				//					JSONObject P = new JSONObject();
				//					P.put("RET", "false");
				//					P.put("MSG", "无效用户，请重新登录");
				//					JSONObject json = new JSONObject();
				//					json.put("A", "");
				//					json.put("A", "");
				//					json.put("RET","false");
				//					json.put("MSG", "无效用户，请重新登录");
				//					strResp = json.toString();
				//					return strResp;
				//				}
				pairs.add(new BasicNameValuePair("U",StringUtil.isNull(currentUser.getUser_id())?"":currentUser.getUser_id()));
				pairs.add(new BasicNameValuePair("M", currentUser.getIs_monitor()+""));
				pairs.add(new BasicNameValuePair("S", StringUtil.isNull(currentUser.getSite_id())?"":currentUser.getSite_id()));
			}
		} catch (Exception e2) {
			L.printStackTrace(e2);
		}
		HttpEntity entity = null;
		if(files!=null&&files.size()>0){

			MultipartEntity builder = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			//builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
		//	strentity = new StringBody(obj.toString(), Charset.forName(HTTP.UTF_8));
			for(NameValuePair pair:pairs){
				strentity = new StringBody(pair.getValue(), Charset.forName(HTTP.UTF_8));
				builder.addPart(pair.getName(),strentity );
			}
			int fCount = 1;

			if (files != null) {
				for (String filename : files) {
					try {
						//						byte[] bit = BitmapUtil.compressImage2Byte(filename,300);
						//builder.addBinaryBody("File" + fCount, BitmapUtil.compressImage2Byte(filename,300));
						builder.addPart("file" + fCount, 
								new FileBody((new File(filename))));
						//						builder.addBinaryBody("File" + fCount, BitmapUtil.compressImage2ByteArrayInputStream(filename,300));   //by zhouchen for file[](php style)
						fCount++;
					} catch (Exception e) {
						SharedPrefUtil.Log("Image 压缩错误：" +e.getMessage());
						L.printStackTrace(e);
					}
				}
			}
			entity = new HttpEntity() {
				
				@Override
				public void writeTo(OutputStream arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isStreaming() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isRepeatable() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isChunked() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public Header getContentType() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public long getContentLength() {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public Header getContentEncoding() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public InputStream getContent() throws IllegalStateException {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void consumeContent() {
					// TODO Auto-generated method stub
					
				}
			};


		}else{
			try {
				entity = new UrlEncodedFormEntity(pairs,HTTP.UTF_8);
			} catch (UnsupportedEncodingException ue) {
				SharedPrefUtil.Log("参数处理：" + ue.getMessage());
				L.printStackTrace(ue);
			}
		}

		HttpPost httpPost = new HttpPost(url);
		User currentUser = userBiz.getCurrentUser();
		String site_id = "0";
		if(currentUser != null && !StringUtil.isNull(currentUser.getSite_id())){
			site_id = currentUser.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}
		httpPost.addHeader("site_id", site_id);
		httpPost.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		L.v(TAG, "HTTP Post: " + url);
		L.v(TAG, "HTTP Post: " + Arrays.toString(pairs.toArray()));
		long currentTimeMillis = System.currentTimeMillis();
		try {
			useCookie();
			httpPost.setEntity(entity);
			SharedPrefUtil.Log("HTTP Post:url " + url+"\r\n"+"参数: " + Arrays.toString(pairs.toArray()));
			HttpResponse resp = httpClient.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				strResp = EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
				SharedPrefUtil.Log(strResp);
				saveCookie();
			} else {
				L.v(TAG, resp.getStatusLine().getReasonPhrase());
				L.v(TAG, resp.getStatusLine().getStatusCode()+"");
				SharedPrefUtil.Log(resp.getStatusLine().getReasonPhrase()+" 响应码："+ resp.getStatusLine().getStatusCode()+"");
				throw (new IOException("服务器响应异常"));
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			SharedPrefUtil.Log(e.getMessage());
			if (e.getMessage() != null && e.getMessage().contains("EPIPE")) {
				count++;
				if (count > RE_TRY_COUNT) {
					count = 0;
					throw (e);
				}
				httpPost.abort();
				return doPost(url, pairs, files);
			} else {
				count = 0;
				httpPost.abort();
				throw e;
			}
		} finally {
			count = 0;
			httpPost.abort();
		}
		L.i(TAG, "time:" + (System.currentTimeMillis() - currentTimeMillis));
		L.i(TAG, "response:" + strResp);
		return strResp;
	}
}