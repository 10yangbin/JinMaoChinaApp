package com.guomao.propertyservice.base;


import com.jinmaochina.propertyservice.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class BaseActivity extends FragmentActivity {
	
	protected RelativeLayout titleLayout;
	protected ImageView leftImageView;
	protected TextView titleTextView;
	protected TextView rightTextView;
	
	public abstract void onLeftButtonClick(View v);
	public abstract void onRightButtonClick(View v);
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}
	
	protected void initNaviView(){
		titleLayout = (RelativeLayout)findViewById(R.id.titleLayout);
		leftImageView = (ImageView)findViewById(R.id.leftImageView);	
		titleTextView = (TextView)findViewById(R.id.titleTextView);	
		rightTextView = (TextView)findViewById(R.id.rightTextView);	
	}	
	

}
