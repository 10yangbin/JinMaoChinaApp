package com.guomao.propertyservice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.openaccount.ui.widget.InputBoxWithHistory;
import com.alibaba.sdk.android.openaccount.util.ResourceUtils;

/**
 * 显示hint 提示的 账号输入框
 */
public class LoginACInputBox extends InputBoxWithHistory{
    public LoginACInputBox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, ResourceUtils.getRStyleableIntArray(context, "inputBox"));
//        OALoginActivity 内做了处理，所以在这里不起作用了
        String hint = typedArray.getString(ResourceUtils.getRStyleable(context, "inputBox_ali_sdk_openaccount_attrs_hint"));
        if (getEditText()!=null)
            getEditText().setHint(hint);

//        隐藏用户输入框的下拉箭头
        TextView historyBtn = (TextView) findViewById("open_history");
        if (historyBtn!=null)
            historyBtn.setVisibility(GONE);
    }
    @Override
    public void showInputHistory(View view, boolean b) {
        super.showInputHistory(view, b);
    }
}
