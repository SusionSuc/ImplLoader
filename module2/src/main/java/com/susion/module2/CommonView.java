package com.susion.module2;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.susion.annotation2.Impl;

/**
 * Created by susion on 2018/10/30.
 */

@Impl(name = "common_view")
public class CommonView extends AppCompatTextView {

    public CommonView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setText("我是Module2 的TextView");
        setBackgroundColor(Color.BLUE);
    }
}
