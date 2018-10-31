package com.susion.implloader;

import android.util.Log;

import com.susion.annotation2.Impl;
import com.susion.commom_bussiness.MainCard;

/**
 * Created by susion on 2018/10/29.
 */
@Impl(name = "main_card_impl1")
public class MainCardImpl1 implements MainCard {

    @Override
    public void sayHello() {
        Log.d("impl-loader", "MainCardImpl1 say hello");
    }

}
