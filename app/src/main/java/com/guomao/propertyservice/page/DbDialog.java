package com.guomao.propertyservice.page;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.adapter.SiteAdapter;
import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.callback.SiteCheckedCallback;
import com.guomao.propertyservice.dao.DB_base;
import com.guomao.propertyservice.eventbus.OnChangeSite;
import com.guomao.propertyservice.eventbus.OnDbDownloadEvent;
import com.guomao.propertyservice.model.user.Site;
import com.guomao.propertyservice.model.user.User;
import com.guomao.propertyservice.mydao.AppAction;
import com.guomao.propertyservice.mydaoimpl.AppActionImpl;
import com.guomao.propertyservice.util.SharedPrefUtil;
import com.guomao.propertyservice.util.StringUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


/**
 * 项目选择的弹出层
 * @author Administrator
 *
 */
public class DbDialog {
	public static DbDialog dbDialog;
	private UserBiz ubiz;
	public AlertDialog dbListDialog = null;
	public static boolean isDbListDialogShown = false;
	static SiteAdapter siteAdapter = null;
	public static List<Site> siteList = new ArrayList<Site>();
	boolean isCancelable = false;
	ListView listView;

	public DbDialog(boolean isCancelable) {
		this.isCancelable = isCancelable;
		EventBus.getDefault().register(this);
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
	}

	public static synchronized DbDialog getInstance(boolean isCancelable) {
		if (dbDialog == null) {
			dbDialog = new DbDialog(isCancelable);
		}
		return dbDialog;
	}

	@SuppressLint("InflateParams")
	public void showdbListDialog(final Activity context,
			final SiteCheckedCallback siteCheckedCallback) {
		/*if (isDbListDialogShown)
			return;*/
		LayoutInflater inflater = LayoutInflater.from(context);// 渲染器
		View customdialog2view = inflater.inflate(R.layout.view_change_site,
				null);
		View title = inflater.inflate(R.layout.dialog_title,
				null);
		listView = (ListView) customdialog2view.findViewById(R.id.list_site);
		Site site = new Site();
		siteList = site.getSiteList(ubiz.getCurrentUser().getSiteList());
		
		if( siteList==null ||siteList.isEmpty()){
			Toast.makeText(context, "未获取到您的项目,请联系系统管理员", Toast.LENGTH_SHORT).show();
			return ;
		}
		for(int i = 0;i<siteList.size();i++){
			site = siteList.get(i);
			if(site==null || site.getSite_id()==null){
				Toast.makeText(context, "存在无效的项目,请联系系统管理员", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		siteAdapter = new SiteAdapter(context, siteList);
		listView.setAdapter(siteAdapter);
		for (int i = 0; i < siteList.size(); i++) {
			String siteId = siteList.get(i).getSite_id();
			String db_base_name = siteId + "base.db";
			String db_business_name = siteId + "business.db";
			if (!DB_base.isDbExists(db_base_name)
					|| !DB_base.isDbExists(db_business_name)) {
				siteList.get(i).setStaus(0);
			} else {
				siteList.get(i).setStaus(1);
			}
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int arg2,
					long arg3) {
				Site sS = siteList.get(arg2);
				String siteId = sS.getSite_id();
				String db_base_name = siteId
						+ "base.db";
				String db_business_name = siteId
						+ "business.db";
				if (DB_base.isDbExists(db_base_name)
						&& DB_base.isDbExists(db_business_name)) {
					User cU = ubiz.getCurrentUser();
					if(cU != null){
						cU.setSite_id(siteId);
						cU.setIs_monitor(sS.getIs_monitor());
						ubiz.getDao().setCurrentUser(cU);
						if (siteList.size() > 0) {
							EventBus.getDefault().post(
									new OnChangeSite(siteId, sS.getIs_monitor()));
							siteCheckedCallback.OnChecked(siteId, sS.getIs_monitor());
						}
					}
					cancelDbListDialog();
				} else {
					Toast.makeText(context, "离线包尚未下载，请下载", Toast.LENGTH_SHORT)
					.show();
				}
			}
		});
		if (siteList.size() > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			//builder.setTitle("切换项目");
			builder.setCustomTitle(title);
			builder.setView(customdialog2view);
			dbListDialog = builder.create();
			dbListDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					isDbListDialogShown = false;
					siteCheckedCallback.OnCancel();
				}
			});
			dbListDialog.show();
			dbListDialog.setCanceledOnTouchOutside(false);
			initDb(context);
			isDbListDialogShown = true;
		} else {
			Toast.makeText(context, "当前只有一个项目", Toast.LENGTH_SHORT).show();
		}
	}

	public void cancelDbListDialog() {
		if (dbListDialog != null && isDbListDialogShown) {
			dbListDialog.dismiss();
			isDbListDialogShown = false;
		}
	}

	public void initDb(Activity context) {
		String siteIdList = "";
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < siteList.size(); i++) {
			stringBuilder.append(siteList.get(i).getSite_id());
			stringBuilder.append(",");
		}
		siteIdList = stringBuilder.toString();
		AppAction appAction = new AppActionImpl();
		appAction.checkDbUpdate(context, siteIdList);
	}

	public static void notifyListViewDataByServer(Activity context,
			String message) {
		if (message == null) {
			return;
		}
		JSONObject jsonObject = null;
        try {
			jsonObject = new JSONObject(message);
		} catch (JSONException e1) {
		}
		JSONArray jsonArray = null;
		if(jsonObject!= null){
			jsonArray= jsonObject.optJSONArray("retdata");
		}
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);


				String siteId = json.getString("site_id");

				int index= getSiteIndex(siteId);
				if(index<0) continue;
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


				siteList.get(index).setBasePath(json.getString("basicpath"));
				siteList.get(index).setBusinessPath(json.getString("busipath"));
				siteList.get(index).setBaseVer(json.getString("basicversion"));
				siteList.get(index).setBusinessVer(json.getString("busiversion"));
				if (!DB_base.isDbExists(db_base_name)
						|| !DB_base.isDbExists(db_business_name)) {
					siteList.get(index).setStaus(0);
				} else if (P_baseVer.equals(json.getString("basicversion"))
						&& P_businessVer.equals(json.getString("busiversion"))) {
					siteList.get(index).setStaus(1);
				} else if (!P_baseVer.equals(json.getString("basicversion"))
						|| !P_businessVer.equals(json.getString("busiversion"))) {
					siteList.get(index).setStaus(2);
				}
			}
			EventBus.getDefault().post(new OnDbDownloadEvent());
		} catch (Exception e) {
			e.printStackTrace();
			DbDialog.dbDialog.cancelDbListDialog();
		}

	}

    @Subscribe
	public void onEvent(OnDbDownloadEvent onDbDownloadEvent) {
		// siteAdapter.notifyChanged();
		if (listView == null) {
			return;
		}
		listView.post(new Runnable() {

			@Override
			public void run() {
				siteAdapter.notifyDataSetChanged();
			}
		});
	}

	public static int getSiteIndex(String site_id){
		if(siteList == null && !siteList.isEmpty() && StringUtil.isNull(site_id)){
			return -1;	
		}
		Site site = null;
		for(int i=0;i< siteList.size();i++){
			site = siteList.get(i);
			if(site == null) continue;
			if(site_id.equalsIgnoreCase(site.getSite_id())){
				return i;
			}
		}
		return -1;
	}
}
