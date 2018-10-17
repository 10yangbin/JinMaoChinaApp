package com.guomao.propertyservice.widget.tel;

import java.io.IOException;

import com.guomao.propertyservice.config.Const;
import com.guomao.propertyservice.javascripinterface.LongforObject;
import com.guomao.propertyservice.util.L;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;  

public class TelListener extends Service{  

	private boolean isIncomming = false;
	private static TelephonyManager telManager;
	private static TelListner listener;

	@Override  
	public void onCreate() {  
		/* 取得电话服务 */  
		if(telManager == null)
			telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
		if(listener == null)
			listener = new TelListner() ;  
		// 监听电话的状态  
		telManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);  
		super.onCreate();  
	}  

	public static void stopListen(){
		if(telManager != null && listener != null){
			telManager.listen(listener, PhoneStateListener.LISTEN_NONE);
		}
	}


	@SuppressLint("SimpleDateFormat")
	class TelListner extends PhoneStateListener{  
		private String number;//定义一个监听电话号码  
		private boolean isRecord;//定义一个当前是否正在复制的标志  
		private MediaRecorder recorder;//媒体复制类  

		@Override  
		public void onCallStateChanged(int state, String incomingNumber) {  
			switch (state) {  
			case TelephonyManager.CALL_STATE_IDLE:/* 无任何状态 */  
				number = null;  
				if (recorder != null && isRecord) {  
					Log.e("msg", "record ok"); 
					closeSpeaker(TelListener.this);
					recorder.stop();//录音完成  
					recorder.reset();   
					recorder.release();  
					isRecord = false;//录音完成，改变状态标志  
					stopListen();
					sendBroadcast(new Intent(Const.ACTION_RECODER_END));
					stopSelf();
				}  
				break;  
			case TelephonyManager.CALL_STATE_OFFHOOK:/* 接起电话 */  
				if(isIncomming){
					return;
				}
				// 录制声音，这是录音的核心代码  
				number=incomingNumber;  
				L.e("msg", "recording");  
				openSpeaker(TelListener.this);
				recorder = new MediaRecorder();  
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 定义声音来自于麦克风  
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//存储格式  
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置编码  
				recorder.setOutputFile(LongforObject.RECODERPATH);  
				try {  
					recorder.prepare();  
					recorder.start(); // 开始刻录  
					isRecord = true;  
				} catch (IllegalStateException e) {  
					e.printStackTrace();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  catch(RuntimeException e){
					if(recorder != null){
						recorder.stop();  
						recorder.reset();   
						recorder.release();  
						isRecord = false;
					}
				}
				break;  
			case TelephonyManager.CALL_STATE_RINGING:/* 电话进来 */  
				L.e("msg", "coming");  
				number = incomingNumber; 
				isIncomming = true;
				break;  

			default:  
				break;  
			}  
		}  

	}  

	@Override  
	public IBinder onBind(Intent arg0) {  

		return null;  
	}  
	/**
	 * 免提
	 * 
	 * @param context
	 */
	private void openSpeaker(Context context)
	{
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setMode(AudioManager.MODE_IN_CALL);
		am.setSpeakerphoneOn(true);
	}

	/**
	 * 免提
	 * 
	 * @param context
	 */
	private void closeSpeaker(Context context)
	{
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setMode(AudioManager.MODE_IN_CALL);
		am.setSpeakerphoneOn(false);
	}
}  