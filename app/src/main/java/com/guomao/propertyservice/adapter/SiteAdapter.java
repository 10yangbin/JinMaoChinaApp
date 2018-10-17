package com.guomao.propertyservice.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.jinmaochina.propertyservice.R;
import com.guomao.propertyservice.biz.UserBiz;
import com.guomao.propertyservice.biz.manger.BizManger;
import com.guomao.propertyservice.biz.manger.BizManger.BizType;
import com.guomao.propertyservice.eventbus.OnDbDownloadEvent;
import com.guomao.propertyservice.main.MainApplication;
import com.guomao.propertyservice.model.user.Site;
import com.guomao.propertyservice.mydao.AppAction;
import com.guomao.propertyservice.mydaoimpl.AppActionImpl;
import com.guomao.propertyservice.network.NetworkCallBack;
import com.guomao.propertyservice.network.request.DownLoadFileVo;
import com.guomao.propertyservice.util.DataFolder;
import com.guomao.propertyservice.util.FileUtil;
import com.guomao.propertyservice.util.SharedPrefUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;


public class SiteAdapter extends BaseAdapter {
	Activity context;
    private List<Site> siteList;
	public HashMap<String, Boolean> checkState;
	private UserBiz ubiz;

	public SiteAdapter(Activity context, List<Site> siteList) {
		this.context = context;
		this.siteList = siteList;
		ubiz = (UserBiz) BizManger.getInstance().get(BizType.USER_BIZ);
		checkState = new HashMap<String, Boolean>();
		for (int i = 0; i < siteList.size(); i++) {
			checkState.put(siteList.get(i).getSite_id(), false);
		}
	}

	@Override
	public int getCount() {
		return siteList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return siteList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(context);
		if (view == null) {
			view = inflater.inflate(R.layout.item_site, null);
			holder = new ViewHolder();
			holder.siteName = (TextView) view.findViewById(R.id.tv_siteName);
			holder.dbInfo = (TextView) view.findViewById(R.id.tv_db_info);
			holder.operate = (Button) view.findViewById(R.id.btn_operate);
			holder.checkBox = (CheckBox) view.findViewById(R.id.isChecked);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Site site = siteList.get(position);
		if (ubiz.getCurrentUser() != null) {
			if (ubiz.getCurrentUser().getSite_id().equals(site.getSite_id())) {
				holder.siteName.setText(Html.fromHtml("<b>" + site.getSite_name()
						+ "</b><font color = '#ff0000'>(当前项目)</font>"));
				ubiz.getCurrentUser().setSite_id(site.getSite_id());
			} else {
				holder.siteName.setText(site.getSite_name());
			}
		}
		switch (site.getStaus()) {
			case 0:
				holder.operate.setEnabled(true);
				holder.operate.setText("下载");
				holder.dbInfo.setText(Html.fromHtml(""
						+ "<font color = '#ff0000'>(未下载)</font>"));
				break;
			case 1:
				holder.dbInfo.setText("已下载");
				holder.operate.setEnabled(false);
				holder.operate.setText("下载");
				break;
			case 2:
				holder.operate.setEnabled(true);
				holder.operate.setText("更新");
				holder.dbInfo.setText("有更新");
				break;
			case 3:
				holder.operate.setEnabled(false);
				holder.operate.setText("下载");
				holder.dbInfo.setText("正在下载");
				break;
			case 4:
				holder.operate.setEnabled(true);
				holder.operate.setText("重新下载");
				holder.dbInfo.setText(Html.fromHtml(""
						+ "<font color = '#ff0000'>(下载失败)</font>"));
				break;
		}

		holder.dbInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		holder.operate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				siteList.get(position).setStaus(3);
				EventBus.getDefault().post(new OnDbDownloadEvent());
				try {
					
					String baseVer = siteList.get(position).getBaseVer();
					int ret = (siteList.get(position).getBaseVer())
							.compareTo(SharedPrefUtil.getDbVersion(siteList
									.get(position).getSite_id() + "baseVer"));
					if (ret > 0) {
						downLoadBaseDb(position);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					int ret = (siteList.get(position).getBusinessVer())
							.compareTo(SharedPrefUtil
									.getDbVersion(siteList.get(position)
											.getSite_id() + "businessVer"));
					if (ret > 0) {
						downLoadBusinessDb(position);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		holder.checkBox.setTag(siteList.get(position).getSite_id());
		if (checkState.get(siteList.get(position).getSite_id())) {
			holder.checkBox.setChecked(true);
		} else {
			holder.checkBox.setChecked(false);
		}
		holder.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(context, "请先下载数据包", Toast.LENGTH_SHORT).show();
				selectNone();
			}
		});

		return view;
	}

	/**
	 * 下载base数据表
	 * @param position
	 */
	public void downLoadBaseDb(final int position) {
		try {
			AppAction appAction = new AppActionImpl();
			DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
			downLoadFileVo.setUrl(siteList.get(position).getBasePath());
			downLoadFileVo.setName(siteList.get(position).getSite_id()
					+ "base.zip");
			downLoadFileVo.setFilename(DataFolder.getAppDataRoot() + "db/zip/"
					+ siteList.get(position).getSite_id() + "base.zip");
			downLoadFileVo.setTarget(DataFolder.getAppDataRoot() + "db/zip/"
					+ siteList.get(position).getSite_id() + "base.zip");
			File file = new File(DataFolder.getAppDataRoot() + "db/zip/"
					+ siteList.get(position).getSite_id() + "base.zip");
			if (file.exists()) {
				file.delete();
			}
			siteList.get(position).setBaseDownloading(true);
			appAction.downLoadFile(context, downLoadFileVo,
					new NetworkCallBack() {

						@Override
						public void onLoading(int progress) {

						}

						@Override
						public void onLoadSuccess(Object obj) {
							siteList.get(position).setBaseDownloading(false);
							initDb(siteList.get(position).getBaseVer(),
									siteList.get(position).getSite_id(),
									siteList.get(position).getSite_id()
											+ "base.zip", siteList
											.get(position).getSite_id()
											+ "baseVer");
							if (!siteList.get(position).isBaseDownloading()
									&& !siteList.get(position)
											.isBusinessDownloading()) {
								siteList.get(position).setStaus(1);
								EventBus.getDefault().post(
										new OnDbDownloadEvent());
								String sId	= siteList.get(position).getSite_id();
//								String sId  = ubiz.getCurrentUser().getSite_id();
//								if(sid1.equals(sId)){
									if(MainApplication.siteUpdate.containsKey(sId)){
										MainApplication.siteUpdate.remove(sId);
									}
//								}
							}
						}

						@Override
						public void onLoadFail() {
							siteList.get(position).setStaus(4);
							EventBus.getDefault().post(new OnDbDownloadEvent());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载业务数据表
	 * @param position
	 */
	public void downLoadBusinessDb(final int position) {
		try {
			AppAction appAction = new AppActionImpl();
			DownLoadFileVo downLoadFileVo = new DownLoadFileVo();
			downLoadFileVo.setUrl(siteList.get(position).getBusinessPath());
			downLoadFileVo.setName(siteList.get(position).getSite_id()
					+ "business.zip");
			downLoadFileVo.setFilename(DataFolder.getAppDataRoot() + "db/zip/"
					+ siteList.get(position).getSite_id() + "business.zip");
			downLoadFileVo.setTarget(DataFolder.getAppDataRoot() + "db/zip/"
					+ siteList.get(position).getSite_id() + "business.zip");
			File file = new File(DataFolder.getAppDataRoot() + "db/zip/"
					+ siteList.get(position).getSite_id() + "business.zip");
			if (file.exists()) {
				file.delete();
			}
			appAction.downLoadFile(context, downLoadFileVo,
					new NetworkCallBack() {

						@Override
						public void onLoading(int progress) {

						}

						@Override
						public void onLoadSuccess(Object obj) {
							siteList.get(position)
									.setBusinessDownloading(false);
							initDb(siteList.get(position).getBusinessVer(),
									siteList.get(position).getSite_id(),
									siteList.get(position).getSite_id()
											+ "business.zip",
									siteList.get(position).getSite_id()
											+ "businessVer");
							if (!siteList.get(position).isBaseDownloading()
									&& !siteList.get(position)
											.isBusinessDownloading()) {
								siteList.get(position).setStaus(1);
								EventBus.getDefault().post(
										new OnDbDownloadEvent());
								String sId	= siteList.get(position).getSite_id();
//								String sId  = ubiz.getCurrentUser().getSite_id();
//								if(sid1.equals(sId)){
									if(MainApplication.siteUpdate.containsKey(sId)){
										MainApplication.siteUpdate.remove(sId);
									}
//								}
							}
						}

						@Override
						public void onLoadFail() {
							siteList.get(position).setStaus(4);
							EventBus.getDefault().post(new OnDbDownloadEvent());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCheckSite() {
		String checkSite = null;
		for (int i = 0; i < siteList.size(); i++) {
			if (checkState.get(siteList.get(i).getSite_id())) {
				return siteList.get(i).getSite_id();
			}
		}
		return checkSite;
	}

	public void setState(String tag) {
		for (int i = 0; i < siteList.size(); i++) {
			checkState.put(siteList.get(i).getSite_id(), false);
		}
		checkState.put(tag, true);
		notifyDataSetChanged();
	}

	public void selectAll() {
		for (int i = 0; i < siteList.size(); i++) {
			checkState.put(siteList.get(i).getSite_id(), true);
			notifyDataSetChanged();
		}
	}

	public void selectNone() {
		for (int i = 0; i < siteList.size(); i++) {
			checkState.put(siteList.get(i).getSite_id(), false);
		}
		notifyDataSetChanged();
	}

	/**
	 * 获取db的压缩文件路径，解压 
	 * @param version
	 * @param site_id
	 * @param fileName
	 * @param p_key
	 */
	private void initDb(String version, String site_id, String fileName,
			String p_key) {
		String filePath = DataFolder.getAppDataRoot() + "db/zip/" + fileName;
		File file = new File(filePath);
		if (file.exists()) {
			try {
				FileUtil.upZipFile(file, site_id, DataFolder.getAppDataRoot()
						+ "db/");
			SharedPrefUtil.putDbVersion(p_key, version);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class ViewHolder {
		TextView siteName;
		TextView dbInfo;
		Button operate;
		CheckBox checkBox;
	}
}
