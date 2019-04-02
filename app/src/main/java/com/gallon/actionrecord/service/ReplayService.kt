package com.gallon.actionrecord.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.gallon.actionrecord.model.Action
import com.gallon.actionrecord.util.ActionHelper
import com.gallon.actionrecord.util.FINISH

/**
 * Created by Gallon2 on 2019/4/2.
 */
class ReplayService : Service() {
    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //把service设置为前台运行，避免手机系统自动杀掉改服务
        val notification = Notification()
        startForeground(startId, notification)

        val type = intent.getStringExtra("type")
        if (type == FINISH) stopSelf()
        val extra = intent.getSerializableExtra("action") ?: return super.onStartCommand(intent, flags, startId)
        Thread { ActionHelper.play(extra as Action) }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}