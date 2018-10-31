package com.susion.implloadercore

import android.content.Context
import android.view.View
import com.susion.annotation2.ProtocolConstants
import java.lang.reflect.Constructor
import java.util.*

/**
 * Created by susion on 2018/10/27.
 * 实例化 @Impl 注解标注的类
 *
 * implMap 保存 @Impl 标注的类的信息
 *
 */
object ImplLoader {

    /**
     * IMPL_LOADER_HELP_CLASS 是利用 asm库 动态生成的 class 文件。
     * */
    @JvmStatic
    fun init() {
        try {
            Class.forName(ProtocolConstants.IMPL_LOADER_HELP_CLASS)
                    .getMethod(ProtocolConstants.IMPL_LOADER_HELP_INIT_METHOD)
                    .invoke(null)
        } catch (e: Exception) {

        }
    }

    //已经注册的类
    private val implMap = HashMap<String, Class<*>>()

    //注册一个实现类, 代码由反射生成的文件调用
    @JvmStatic
    fun registerImpl(implName: String, implClass: Class<*>) {
        implMap.put(implName, implClass)
    }

    /**
     * 获得一个类的实例
     *
     * 被实例化的类必须含有默认构造函数 ！！！！
     */
    @JvmStatic
    fun <T> getImplInstance(implName: String): T? {
        val rtnClazz = implMap[implName] as? Class ?: return null

        try {
            return rtnClazz.newInstance() as T
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmStatic
    fun getCurrentImplClassSize() = implMap.size


    /**
     * 获得一个View类的实例
     *
     * 被实例化的类必须含有 constructor(view) 的构造函数
     */
    @JvmStatic
    fun getView(context: Context, viewName: String): View? {
        val rtnClazz = implMap[viewName] ?: return null

        val constructors = rtnClazz.constructors
        var ctxConstructor: Constructor<View>? = null
        val contextClazz = Context::class.java
        constructors.forEach {
            if (it.parameterTypes.size == 1) {
                if (it.parameterTypes[0] == contextClazz) {
                    ctxConstructor = it as Constructor<View>?
                }
            }
        }

        if (ctxConstructor == null) return null

        try {
            return ctxConstructor?.newInstance(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    //can get more, please extension .......
}