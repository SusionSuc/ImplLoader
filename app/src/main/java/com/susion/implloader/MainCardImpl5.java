package com.susion.implloader;

import android.util.Log;

import com.susion.annotation2.Impl;
import com.susion.commom_bussiness.MainCard;

/**
 * Created by susion on 2018/10/30.
 */
@Impl(name = "main_card_impl5")
public class MainCardImpl5 implements MainCard{

    @Override
    public void sayHello() {
        Log.d("impl-loader", "MainCardImpl5");
    }

}
