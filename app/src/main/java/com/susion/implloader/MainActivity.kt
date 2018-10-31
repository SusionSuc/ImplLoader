package com.susion.implloader

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.susion.annotation2.ProtocolConstants
import com.susion.commom_bussiness.MainCard
import com.susion.implloadercore.ImplLoader
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by susion on 2018/10/30.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //初始化标记的注解实现类
        ImplLoader.init()

        Log.d(ProtocolConstants.TAG, "一共加载了: " + ImplLoader.getCurrentImplClassSize() + " 个 Impl 类")

        val mainCardImpl = ImplLoader.getImplInstance<MainCard>("main_card_impl1")
        mainCardImpl?.sayHello()

        //跨模块加载fragment
        val reuseFragmet = ImplLoader.getImplInstance<Fragment>("reuse_fragment")

        //获得 module2 的TextView
        val commonView = ImplLoader.getView(this, "common_view")
        mMainRoot.addView(commonView)

        //获得 module1 的 page
        val module1Page = ImplLoader.getView(this, "module1_page")
        mMainRoot.addView(module1Page)

        //获得module2的一个kotlin view
        val kotlinView = ImplLoader.getView(this, "kotlin_page")
        mMainRoot.addView(kotlinView)


    }
}
