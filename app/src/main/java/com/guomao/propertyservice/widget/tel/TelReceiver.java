package com.guomao.propertyservice.widget.tel;
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
  
public class TelReceiver extends BroadcastReceiver {  
  
    final String tel="android.intent.action.PHONE_STATE";  
    @Override  
    public void onReceive(Context context, Intent intent) {  
          
        if(intent!=null){  
            if(Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())){  
                Intent i=new Intent(context, TelListener.class);  
                context.startService(i);  
            }  
        }  
    }  
  
}  