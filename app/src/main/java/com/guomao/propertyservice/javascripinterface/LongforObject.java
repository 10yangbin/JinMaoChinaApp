package com.guomao.propertyservice.javascripinterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.callback.SiteCheckedCallback;
import com.guomao.propertyservice.communcation.OfflineOperateComImpl;
import com.guomao.propertyservice.communcation.WebClient;
import com.guomao.propertyservice.config.CommenUrl;
import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.dao.OfflineOperateDaoImpl;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.dao.DB_business;
import com.guomao.propertyservice.eventbus.OnOfflineUploadSucc;
import com.guomao.propertyservice.page.DbDialog;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.page.LoginActivity2;
import com.guomao.propertyservice.service.OfflineData2Service;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateCom;
import com.guomao.propertyservice.model.offlineoperate.OfflineOperateDao;
import com.guomao.propertyservice.model.offlineoperate.Operation;
import com.guomao.propertyservice.model.user.Site;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.network.OperateCallBack;
import com.guomao.propertyservice.network.bean.NetworkBeanArray;
import com.guomao.propertyservice.network.core.BaseNetWork;
import com.guomao.propertyservice.network.request.RequestVo;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.FileProvider7JinMao;
import com.guomao.propertyservice.util.FunctionUtil;
import com.guomao.propertyservice.util.GsonUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;
import com.guomao.propertyservice.widget.barcode.MipcaActivityCapture;
import com.guomao.propertyservice.widget.imageselect.SelectPicActivity;
import com.guomao.propertyservice.page.WebViewActivity;
import com.guomao.propertyservice.widget.mark.ImageActivity;
import com.guomao.propertyservice.widget.mark.ShowImageActivity;
import com.guomao.propertyservice.widget.sign.DialogListener;
import com.guomao.propertyservice.widget.sign.SignActivity;
import com.guomao.propertyservice.widget.CommomDialog;
import com.guomao.propertyservice.widget.LinkToast;
import com.guomao.propertyservice.widget.viewimage.ViewImagesActivity;
import com.jinmaochina.propertyservice.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.guomao.propertyservice.page.MainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;


/**
 * 该类可以提供所有的方法让javascript来调用，window.longfor.方法名。
 * 
 * @author Administrator
 * 
 */
public class LongforObject extends BaseNetWork{
	protected static final String TAG = null;
	private Context context;
	private WebView webView;
	private Gson gson = GsonUtil.gson();
	private UserBiz userBiz;
	public static String MPHOTOPATH;
	public static String WRITEPATH;
	public static String RECODERPATH;
	String DB_NAME = null;
	private static UserBiz ubiz;
	//照相
	public static final int PIC_RESQUEST=1;
	//图纸的选择
	public static final int PIC_SAVE=2;
	//图片的选择
	public static final int SELECT_SAVE_PIC=3;
	public LongforObject(WebView webView) {
		this.webView = webView;
		this.context = webView.getContext();
		userBiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);

	}

	/**
	 * 
	 * 启动Activity 扫描二维码 调用该方法使用window.csq_hw.scanQR() 成功回调方法
	 * scanBarcodeSuccessCallBack(String result)
	 */
	@JavascriptInterface
	public void scanQR() {

		Intent intent = new Intent(context, MipcaActivityCapture.class);
		if (context instanceof WebViewActivity) {
			((WebViewActivity) context).startActivityForResult(intent,
					WebViewActivity.SCAN_BARCODE_REQUEST);
		} else if (context instanceof MainActivity) {
			((MainActivity) context).startActivityForResult(intent,
					WebViewActivity.SCAN_BARCODE_REQUEST);
		} else if (MainActivity.mainActivity != null){
			MainActivity.mainActivity.startActivityForResult(intent,
					WebViewActivity.SCAN_BARCODE_REQUEST);
		}
	}
	
	@JavascriptInterface
	public String getApkVersionName() {
		return FunctionUtil.getVersionName(MainApplication.getInstance());
	}


	/**
	 * js 调android 原生对话框
	 */
	@JavascriptInterface
	public void windowAlert() {
		if(MainActivity.mainActivity != null){
			Dialog dialog = new Dialog(MainActivity.mainActivity);
			dialog.show();
		}
	}


	/**
	 * js 调android项目选择的弹出框
	 */
	@JavascriptInterface
	public void showSelectSiteDialog() {
		if (context instanceof WebViewActivity) {
			DbDialog.getInstance(false).showdbListDialog(
					(WebViewActivity) context, new SiteCheckedCallback() {
						@Override
						public void OnChecked(String siteId, String isMonitor) {}
						@Override
						public void OnCancel() {}
					});
		} else if (context instanceof MainActivity) {
			DbDialog.getInstance(false).showdbListDialog(
					(MainActivity) context, new SiteCheckedCallback() {
						@Override
						public void OnChecked(String siteId, String isMonitor) {}
						@Override
						public void OnCancel() {}
					});
		}else if (MainActivity.mainActivity != null){
			DbDialog.getInstance(false).showdbListDialog(
					MainActivity.mainActivity, new SiteCheckedCallback() {
						@Override
						public void OnChecked(String siteId, String isMonitor) {}
						@Override
						public void OnCancel() {}
					});
		}
	}


	/**
	 * js 调android获取当前项目Name的方法
	 */
	@JavascriptInterface
	public String getCurrentSiteName() {
		List<Site> siteList = null;
		Site site = new Site();
		siteList = site.getSiteList(userBiz.getCurrentUser().getSiteList());
		HashMap<String, String> siteMap = new HashMap<String, String>();
		for (int i = 0; i < siteList.size(); i++) {
			siteMap.put(siteList.get(i).getSite_id(), siteList.get(i).getSite_name());
		}
		String site_name = siteMap.get(userBiz.getCurrentUser().getSite_id());
		return site_name;
	}


	/**
	 * js 调android获取当前项目更新状态的方法
	 */
	@JavascriptInterface
	public boolean getCurrentSiteUpdate(){
		String sId = getcurrUserSiteID();
		if(MainApplication.siteUpdate.containsKey(sId)){
			return MainApplication.siteUpdate.get(sId);
		}
		return false;
	}


	/**
	 * 离线数据的更新，仅有MainActivity 中的 onResume使用,后续可以优化，删除它
	 */
	public static void getDbUpdate() {
		List<String> siteIds = getSiteIds();
		//if(siteIds == null || siteIds.trim().length()==0) return;
		if(siteIds == null || siteIds.size()<=0) return;

		final HttpUtils http = new HttpUtils();
		RequestVo mRequestVo = new RequestVo();
		mRequestVo.setRequestUrl(CommenUrl.checkDbUpdate);
		mRequestVo.setRequestMethod(HttpMethod.POST);
		RequestParams params = new RequestParams();
		//params.addBodyParameter("site_id", siteIds);

		JSONArray arr = new JSONArray(siteIds);
		JSONObject obj = new JSONObject();
		try {
			obj.put("site_ids", arr);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		params.addBodyParameter(new BasicNameValuePair("reqjson",obj.toString()));
		mRequestVo.setRequestParams(params);
		http.configSoTimeout(10 * 1000);

		String site_id = "0";
		User u = ((UserBiz) BizManger.getInstance().get(BizType.USER_BIZ)).getCurrentUser();
		if(u != null && !StringUtil.isNull(u.getSite_id())){
			site_id = u.getSite_id();
		}else{
			site_id = SharedPrefUtil.getMsg("LogOutSiteId","SiteId");
			if(StringUtil.isNull(site_id)){
				site_id = "0";
			}
		}

		//params.addHeader("site_ids", Arrays.toString(siteIds.toArray(new String[siteIds.size()])));
		//params.addHeader("site_ids", siteIds.toString());
		//params.addHeader("version",FunctionUtil.getVersionName(MainApplication.getInstance()));
		useCookie(http);
		http.send(HttpMethod.POST, CommenUrl.checkDbUpdate, params,new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {

				saveCookie(http);

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {

				if(arg0!=null&& !StringUtil.isNull(arg0.result)){
					try {
						JSONObject jsonObject = null;
                        try {
							jsonObject = new JSONObject(arg0.result);
						} catch (JSONException e1) {
						}
						JSONArray jsonArray = null;
						if(jsonObject!= null){
							jsonArray= jsonObject.optJSONArray("retdata");
						}
						if(jsonArray.length()>0){
							JSONObject json = null;

							for (int i = 0; i < jsonArray.length(); i++) {
								json = jsonArray.optJSONObject(i);
								if(json == null){
									continue;
								}
								String siteId = json.optString("site_id");
								/*String business_path = json.optString("busipath");
								String basic_path = json.optString("basicpath");
								ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
								ubiz.getCurrentUser().setSiteList(siteId);*/

								String baseVer = siteId + "baseVer";
								String businessVer = siteId + "businessVer";
								String db_base_name = siteId + "base.db";
								String db_business_name = siteId + "business.db";
								String P_baseVer = null;
								String P_businessVer = null;
								try {
									P_baseVer = SharedPrefUtil.getDbVersion(baseVer);
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									P_businessVer = SharedPrefUtil.getDbVersion(businessVer);
								} catch (Exception e) {
									e.printStackTrace();
								}
								P_baseVer = P_baseVer == null ? "0" : P_baseVer;
								P_businessVer = P_businessVer == null ? "0" : P_businessVer;

								//如果手机中不存在db,
								if(!DB_base.isDbExists(db_base_name)
										|| !DB_base.isDbExists(db_business_name))
									MainApplication.siteUpdate.put(siteId, false);

								if(!P_baseVer.equals(json.optString("basicversion"))
										|| !P_businessVer.equals(json.optString("busiversion"))){
									MainApplication.siteUpdate.put(siteId, true);
									MainApplication.siteUpdateAddress.put(siteId, json.toString());
								}else{
									MainApplication.siteUpdate.put(siteId, false);
								}
							}
						}
					} catch (Exception e) {
					}
				}
				saveCookie(http);
			}

		} );
	}


	/*	@JavascriptInterface
	public boolean getSiteUpdate(){
		//String[] sites = getSiteIds().split(",");
		List<String> sites = getSiteIds();
		for(String site: sites){
			if(StringUtil.isNull(site)) continue;
			Boolean value = MainApplication.siteUpdate.get(site);
			if(value!=null && value){
				return true;
			}
		}
		return false;
	}*/

	private static List<String> getSiteIds() {
		UserBiz biz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		User u =  biz.getCurrentUser();
		List<String>  siteIds = new ArrayList<>();
		if(u!=null && u.getSiteList()!=null){
			List<Site> sites = new Site().getSiteList(u.getSiteList());
			for(Site site :sites){
				if(site !=null){
					//siteIds.append(site.getSite_id()).append(",");
					siteIds.add(site.getSite_id());
				}
			}
			/*if(siteIds.length()>1){
				siteIds.deleteCharAt(siteIds.length()-1);
			}*/
		}
		return siteIds;
	}

	/**
	 * js 调android获取apk 的版本名字
	 */
	@JavascriptInterface
	public String getVersion() {
		String version = "";
		version = FunctionUtil.getVersionName(context);
		return version;
	}


	/**
	 * 离线报事的web方法
	 * @param isShowProgress
	 */
	@JavascriptInterface
	public void uploadOfflineData(boolean isShowProgress) {
		UserBiz ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		if (!FunctionUtil.isServiceWork(context,
				"com.guomao.propertyservice.service.OfflineData2Service")
				&& ubiz.getCurrentUser() != null) {
			Intent offineDataIntent = new Intent(context,
					OfflineData2Service.class).putExtra("isShowProgress",
							isShowProgress);// 离线报事
			context.startService(offineDataIntent);
		}
	}

	/**
	 * 离线报事联网自动提交的web方法
	 * @param id
	 */
	@JavascriptInterface
	public String uploadOfflineDataById(String id) {
		if (FunctionUtil.isServiceWork(context,
				"com.guomao.propertyservice.service.OfflineData2Service")) {
			Toast.makeText(context, "后台正在自动提交,请稍候", Toast.LENGTH_SHORT).show();
			return null;
		}
		String result = null;
		String returnStr = null;
		Operation offlineOperate = null;
		Intent intent = new Intent(Const.ACTION_OFFLINE_PER_UPLOAD_START);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainApplication.getInstance().sendBroadcast(intent);
		if(StringUtil.isNull(DB_NAME)){
			String site_id = userBiz.getCurrentUser().getSite_id();
			DB_NAME = site_id + "business.db";
		}
		SQLiteDatabase db = DB_business.db_open(DB_NAME, "", "");
		OfflineOperateCom com = new OfflineOperateComImpl(
				WebClient.getInstance());
		OfflineOperateDao dao = new OfflineOperateDaoImpl(db, db, context);
		offlineOperate = dao.getOfflineOperateById(id);
		MainApplication.isManualSubmit = true;
		if (offlineOperate != null) {
			try {
				result = com.sendOfflineData2Server(offlineOperate);
				offlineOperate = dao.getOfflineOperateById(id);
				NetworkBeanArray networkBeanArray = new NetworkBeanArray(result);
				if (networkBeanArray.isSucc()) {
					offlineOperate.setOperate_status(1);
					EventBus.getDefault().post(new OnOfflineUploadSucc());
				} else {
					offlineOperate.setOperate_status(0);
					Toast.makeText(context, "数据提交失败，请稍后重试", Toast.LENGTH_SHORT)
					.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				offlineOperate.setOperate_status(0);
				Toast.makeText(context, "数据提交失败，请稍后重试", Toast.LENGTH_SHORT)
				.show();
			}
		}
		offlineOperate.setOperate_result(result);
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		offlineOperate.setSubmit_time(date);
		dao.save(offlineOperate);
		if (db != null && db.isOpen()) {
			db.close();
		}
		returnStr = GsonUtil.gson().toJson(offlineOperate).toString();
		Intent endIntent = new Intent(Const.ACTION_OFFLINE_PER_UPLOAD_SUCC);
		endIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainApplication.getInstance().sendBroadcast(endIntent);
		MainApplication.isManualSubmit = false;
		return returnStr;
	}

	/**
	 * 手写签名的调用
	 */
	@JavascriptInterface
	public void writeSign(String price, int selection, int editable) {
		writeSign(null, price, selection, editable);
	}

	/**
	 * 新增的手写签名的方法 添加水印
	 * @param taskCode
	 * @param price
	 * @param selection
	 * @param editable
	 * changed by alang 2015-10-27
	 */
	@JavascriptInterface
	public void writeSign(String taskCode,String price, int selection, int editable) {
		/*
		if(StringUtil.isNull(taskCode)){
			WRITEPATH = DataFolder.getAppDataRoot() + "cache/images/sign_"
					+ String.valueOf((int) (Math.random() * 10000))
					+ System.currentTimeMillis() + ".png";
		}else{
			WRITEPATH = new StringBuilder(DataFolder.getAppDataRoot()).append("cache/images/sign_").append(taskCode).append(".png").toString();
		}*/
		//每次重新生成签名文件
		WRITEPATH = DataFolder.getAppDataRoot() + "cache/images/sign_"
				+ String.valueOf((int) (Math.random() * 10000))
				+ System.currentTimeMillis() + ".png";

		if(MainActivity.mainActivity != null){
			Intent intent = new Intent(context,SignActivity.class);
			intent.putExtra("selection", selection);
			intent.putExtra("editable", editable);
			intent.putExtra("price", price);
			intent.putExtra("orderId", taskCode);
			MainActivity.mainActivity.startActivity(intent);
		}

		SignActivity.setDialogListener(new DialogListener() {
			private Bitmap mSignBitmap;

			public void refreshActivity(Object object,
					final int selection) {
				mSignBitmap = (Bitmap) object;
				createFile();
				webView.post(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:signatureCallback('"
								+ WRITEPATH + "'," + selection + ")");
					}
				});

			}

			private void createFile() {
				File writeFile = new File(WRITEPATH);
				File pF = writeFile.getParentFile();
				if (!pF.exists()) {
					pF.mkdirs();
				}
				//				if (!writeFile.exists()) {
				//					try {
				//						writeFile.createNewFile();
				//					} catch (IOException e) {
				//						e.printStackTrace();
				//					}
				//				}
				try {
					writeFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}				
				ByteArrayOutputStream baos = null;
				try {
					baos = new ByteArrayOutputStream();
					File file = new File(WRITEPATH);
					mSignBitmap.compress(Bitmap.CompressFormat.PNG, 30,
							baos);
					byte[] photoBytes = baos.toByteArray();
					if (photoBytes != null) {
						if (!file.exists()) {
							file.createNewFile();
						}
						OutputStream out = new FileOutputStream(file);
						out.write(photoBytes);
						baos.flush();
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
					if(mSignBitmap != null && !mSignBitmap.isRecycled()){
						mSignBitmap.recycle();
					}
				}
			}
		});

	}



	/**
	 * 启动Activity 打开本地照相机 调用该方法使用window.csq_hw.camera() 成功回调方法
	 * takePhotoSuccessCallBack(String url);
	 */
	@SuppressLint("SimpleDateFormat")
	@JavascriptInterface
	public void camera() {
		
	
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sDateFormat.format(new java.util.Date());
		try {
			if (StringUtil.isNull(userBiz.getUserId())) {
				MPHOTOPATH = DataFolder.getAppDataRoot() + "cache/images/"
						+ date + File.separator + System.currentTimeMillis()
						+ ".jpeg";
			} else {
				MPHOTOPATH = DataFolder.getAppDataRoot() + "cache/images/"
						+ date + File.separator + userBiz.getUserId() + "_"
						+ System.currentTimeMillis() + ".jpeg";
			}

			SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "CAMER_PHOTO_PATH", MPHOTOPATH);
			SharedPrefUtil.setMsg("CAMER_PHOTO_PATH", "ISTAKEPHOTO", "true");
			File mPhotoFile = new File(MPHOTOPATH);
			if (!mPhotoFile.getParentFile().exists()) {
				mPhotoFile.getParentFile().mkdirs();
			}
			if (!mPhotoFile.exists()) {
				mPhotoFile.createNewFile();
			}
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
			intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider7JinMao.getUriForFile(context, mPhotoFile));
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
			if (context instanceof WebViewActivity) {
				((WebViewActivity) context).startActivityForResult(intent,
						WebViewActivity.START_CAMERA_REQUEST);
			} else if (context instanceof MainActivity || MainActivity.mainActivity != null) {
				MainActivity.mainActivity.startActivityForResult(intent,
						WebViewActivity.START_CAMERA_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	/**
	 * js 调android 的Toast提示 
	 */
	@JavascriptInterface
	public void showToast(final String content) {
		if (!StringUtil.isNull(content)) {
			if(context  instanceof Activity){
				((Activity) context).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						LinkToast.makeText(context, content, Toast.LENGTH_SHORT).show();
					}
				});
			}else if(MainActivity.mainActivity != null){
				MainActivity.mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LinkToast.makeText(MainActivity.mainActivity, content, Toast.LENGTH_SHORT).show();
					}
				});
			}

		}
	}

	@JavascriptInterface
	public String getUserId() {
		return userBiz.getUserId();
	}

	/*	@JavascriptInterface
	public String getBaseURL() {
		return Const.BASE_URL;
	}*/

	@JavascriptInterface
	public String getUserInfo() {
		return SharedPrefUtil.getMsg("userinfo", getUserId());
	}

	@JavascriptInterface
	public String getcurrUserSiteID() {
		return userBiz.getCurrentUser().getSite_id();
	}
	
	@JavascriptInterface
	public int getcurrUserType() {
		return userBiz.getCurrentUser().getCf_type();
	}
	@JavascriptInterface
	public String getCurrentUserName() {
		return userBiz.getCurrentUser().getUser_alias();
	}

	@JavascriptInterface
	public String getCurrentUserCode() {
		return userBiz.getCurrentUser().getUser_name();
	}

	@Deprecated
	@JavascriptInterface
	public int isCurrUserMonitor() {
		return 0;
	}

	@JavascriptInterface
	public String getUserPhone() {
		return userBiz.getCurrentUser().getMobile();
	}
	
	@JavascriptInterface
	public String getPayUrl() {
		return MainApplication.PayUrl;
	}

	@JavascriptInterface
	public boolean hasPermission(String code){
		return userBiz.hasPermission(code);
	}

	/**
	 * js调用的退出功能
	 * 
	 */
	@JavascriptInterface
	public void getDispatchPerson() {

		new CommomDialog(MainActivity.mainActivity, R.style.dialog, "您要退出登录吗？", new CommomDialog.OnCloseListener() {
			@Override
			public void onClick(Dialog dialog, boolean confirm) {
				if (confirm){
					dialog.dismiss();
					userExit();
				}

			}
		}).setTitle("提示").show();

	}

	/**
	 * 退出程序，调用用户的业务biz方法
	 */
	public void userExit() {
		if (MainApplication.cancel) {
			try {
				MainApplication.cancel = false;
				Intent intent_user_logout_start = new Intent(
						Const.ACTION_USER_LOGOUT_START);
				intent_user_logout_start
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.sendBroadcast(intent_user_logout_start);
				userBiz.logout(new OperateCallBack() {

					@Override
					public void onTokenInvalid() {
					}

					@Override
					public void onLoadSuccess(Object obj) {
						MainApplication.cancel = true;
						Intent intent = new Intent(MainActivity.mainActivity, LoginActivity2.class);
						MainActivity.mainActivity.startActivity(intent);
						MainActivity.mainActivity.finish();
						//unRegisteXG();  业务里面已经做处理
					}

					@Override
					public void onLoadFail(String msg) {
						Intent intent_user_logout_end = new Intent(
								Const.ACTION_USER_LOGOUT_END);
						intent_user_logout_end
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.sendBroadcast(intent_user_logout_end);
					}
				});
			} catch (Exception e) {
				showToast(e.getMessage());
			}
		} else {
			return;
		}
	}

	/*public void unRegisteXG() {
		XGPushManager.unregisterPush(context, new XGIOperateCallback() {

			@Override
			public void onSuccess(Object arg0, int arg1) {
				List<Site> siteList = new ArrayList<Site>();
				Site site = new Site();
				siteList = site.getSiteList(userBiz.getCurrentUser()
						.getSiteList());
				List<String> tagList = new ArrayList<String>();
				tagList = ubiz.getCurrentUser()
						.getTag();
				for (int i = 0; i < tagList.size(); i++) {
					XGPushManager.deleteTag(context,
							tagList.get(i));
					XGPushManager.deleteTag(context, 
							tagList.get(i));
				}
				Intent intent_user_logout_end = new Intent(
						Const.ACTION_USER_LOGOUT_END);
				intent_user_logout_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.sendBroadcast(intent_user_logout_end);
			}

			@Override
			public void onFail(Object arg0, int arg1, String arg2) {
				List<Site> siteList = new ArrayList<Site>();
				Site site = new Site();
				siteList = site.getSiteList(userBiz.getCurrentUser()
						.getSiteList());
				for (int i = 0; i < siteList.size(); i++) {
					XGPushManager.deleteTag(context, "notMonitor"
							+ siteList.get(i).getSite_id());
					XGPushManager.deleteTag(context, "isMonitor"
							+ siteList.get(i).getSite_id());
				}
				Intent intent_user_logout_end = new Intent(
						Const.ACTION_USER_LOGOUT_END);
				intent_user_logout_end.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.sendBroadcast(intent_user_logout_end);
			}
		});
	}*/

	/**
	 * js 调android 拨打打电话的方法
	 */
	@JavascriptInterface
	public  void dialNumber(String ownerId) {
		//		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ownerId));		
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ownerId));		
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainActivity.mainActivity.startActivity(intent);
	}


	/**
	 * js 调android 拨打打电话及其录音的方法
	 */
	@JavascriptInterface
	public void dialNumberWithRecoder(String orderId){
		if(MainActivity.mainActivity != null){
			MainActivity.mainActivity .registerPhoneStateReceiver();
			File dir = new File(DataFolder.getAppDataRoot()+"recoder/");
			if(!dir.exists()){
				dir.mkdirs();
			}
			File recoderFile = new File(dir,orderId+"_"+new SimpleDateFormat("yy-MM-dd-HH:mm:ss").format(new Date())+".3gp");//3gp
			RECODERPATH = recoderFile.getAbsolutePath();
			SharedPrefUtil.setMsg("RECODER_PATH", "RECODER_PATH", RECODERPATH);
			SharedPrefUtil.setMsg("RECODER_PATH", "ISRECODER", "true");
			Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);		//,Uri.parse("tel:"+"") 修改直接拨打功能为 手动输入电话号码 
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MainActivity.mainActivity.startActivity(intent);
			//String [] strs = new String[]{"","",""};
		}
	}
	
	/**
	 * 报事图纸的选择
	 * 
	 * @param BLId
	 * @param FLId
	 * @param path
	 * @param areaId
	 * @param areaName
	 */
	@JavascriptInterface
	public void onAreaSelected(String BLId,String FLId,String path,String areaId,String areaName){
		if(StringUtil.isNull(areaId)){
			Toast.makeText(context,"不存在该张图纸", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent=new Intent(context,ImageActivity.class);
		intent.putExtra("areaId", areaId);
		MainActivity activity=(MainActivity)context;
		activity.startActivityForResult(intent, LongforObject.PIC_RESQUEST);
	}
	
	@JavascriptInterface
	public  void showImage(String imagePath,int index ) {
		String path[] = imagePath.split(",");
		Intent intent = new Intent(context,ViewImagesActivity.class);		
		intent.putExtra(ViewImagesActivity.IMAGES, path);
		intent.putExtra(ViewImagesActivity.SHOW_INDEX, index);
		MainActivity.mainActivity.startActivity(intent);
	}
	
	/**
	 * 调用本地方法，图纸的展示,从网页获取路径，展示 
	 * @param imagePath
	 */
	@JavascriptInterface
	public void showDrawingImage(String imagePath) {
		Intent intent=new Intent(context,ShowImageActivity.class);
		intent.putExtra("path", imagePath);
		MainActivity activity=(MainActivity)context;
		activity.startActivityForResult(intent, LongforObject.PIC_SAVE);
	}
	
	
	/**
	 * 报事从相册选择图片 ,获取路径，传递到网页
	 */
	@JavascriptInterface
	public void selectImage() {
		Intent intent=new Intent(context,SelectPicActivity.class);
		MainActivity activity=(MainActivity)context;
		activity.startActivityForResult(intent, LongforObject.SELECT_SAVE_PIC);

		/*// 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
		File takePhotoDir = new File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerTakePhoto");
		MainActivity activity = (MainActivity)context;
		Intent photoPickerIntent = new BGAPhotoPickerActivity.IntentBuilder(activity)
				.build();
		activity.startActivityForResult(photoPickerIntent, SELECT_SAVE_PIC);*/
	}
	
	
	
	
		
}


















