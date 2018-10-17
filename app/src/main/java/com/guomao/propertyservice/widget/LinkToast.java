package com.guomao.propertyservice.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jinmaochina.propertyservice.R;

/**
 * 创建日期：2018/10/15 on 14:47
 * 描述:
 * 作者:杨斌 Administrator
 */
public class LinkToast {

    private Toast mToast;

    private LinkToast(Context context, CharSequence text, int duration) {
        this.mToast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.link_toast, (ViewGroup)null);
        TextView tv = (TextView)view.findViewById(R.id.text);
        tv.setText(text);
        this.mToast.setDuration(duration);
        this.mToast.setGravity(17, 0, 0);
        this.mToast.setView(view);
    }

    public static LinkToast makeText(Context context, CharSequence text, int duration) {
        return new LinkToast(context, text, duration);
    }

    public static LinkToast makeText(Context context, int textResId, int duration) {
        String text = context.getResources().getString(textResId);
        return new LinkToast(context, text, duration);
    }

    public void show() {
        this.mToast.show();
    }

    public void setGravity(int gravity, int xoffset, int yoffset) {
        this.mToast.setGravity(gravity, xoffset, yoffset);
    }
}
