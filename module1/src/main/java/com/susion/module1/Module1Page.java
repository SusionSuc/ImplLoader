package com.susion.module1;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.susion.annotation2.Impl;
import com.susion.implloadercore.ImplLoader;

/**
 * Created by susion on 2018/10/30.
 */

@Impl(name = "module1_page")
public class Module1Page extends LinearLayout {

    public Module1Page(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 10;
        setLayoutParams(layoutParams);
        TextView tv = new TextView(getContext());
        tv.setBackgroundColor(Color.RED);
        tv.setText("我是 module1_page 中的TextView, 我会使用module2的textview, 看我马上要使用了 : ...");
        addView(tv);

        View commonView = ImplLoader.getView(getContext(), "common_view");
        addView(commonView);

    }

}
