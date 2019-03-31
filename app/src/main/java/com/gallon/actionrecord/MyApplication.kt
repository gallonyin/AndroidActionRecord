package com.gallon.actionrecord

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * Created by Gallon2 on 2019/3/31.
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }

}