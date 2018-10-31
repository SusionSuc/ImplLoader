package com.susion.module2

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatTextView
import android.view.ViewGroup
import com.susion.annotation2.Impl

/**
 * Created by susion on 2018/10/31.
 */
@Impl(name = "kotlin_page")
class KotlinPage : AppCompatTextView {

    constructor(context: Context?) : super(context) {
        text = "我是一个kotlin text view"
        layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = 20
        }
        setBackgroundColor(Color.GRAY)
    }
}