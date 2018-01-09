package com.hfad.zhyops;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhy on 2017/12/29.
 */

public class MyPreferenceCategory extends PreferenceCategory {
    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(13);//设置title文本的字体大小
            tv.setAllCaps(false);//设置title文本不全为大写
            tv.setTextColor(Color.parseColor("#34b5e5"));//设置title文本的颜色
        }
    }
}
