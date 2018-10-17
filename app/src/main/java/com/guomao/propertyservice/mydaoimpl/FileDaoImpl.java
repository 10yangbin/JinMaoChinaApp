package com.guomao.propertyservice.mydaoimpl;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import com.guomao.propertyservice.mydao.FileDao;
import com.guomao.propertyservice.network.request.DownLoadFileVo;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.util.Log;

/**
 * 数据库，图片的下载
 * @author Administrator
 *
 */
public class FileDaoImpl implements FileDao {
	private String TAG = FileDaoImpl.class.getSimpleName();
	private static ConcurrentHashMap<String, DownLoadFileVo> downLoadTags = new ConcurrentHashMap<String, DownLoadFileVo>();

	@Override
	public void downLoadFile(final DownLoadFileVo downLoadFileVo) {

		if(downLoadTags.containsKey(downLoadFileVo.getUrl()+downLoadFileVo.getTarget())){
			return;
		}else{
			downLoadTags.put(downLoadFileVo.getUrl()+downLoadFileVo.getTarget(), downLoadFileVo);
		}
		HttpUtils http = new HttpUtils();
		RequestParams requestParams = new RequestParams();
		requestParams.addBodyParameter("filename", downLoadFileVo.getFilename());
		http.download(HttpMethod.POST,downLoadFileVo.getUrl(), downLoadFileVo.getTarget(),requestParams,
				true, true, new RequestCallBack<File>() {
			@Override
			public void onStart() {
			}

			@Override
			public void onLoading(long total, long current,
					boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<File> responseInfo) {
				Log.d(TAG, responseInfo.toString());
				downLoadTags.remove(downLoadFileVo.getUrl()+downLoadFileVo.getTarget());
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.d(TAG, msg);
				downLoadTags.remove(downLoadFileVo.getUrl()+downLoadFileVo.getTarget());
			}
		});
	}
}
