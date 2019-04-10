package com.gallon.actionrecord.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //把service设置为前台运行，避免手机系统自动杀掉改服务
        val notification = Notification()
        startForeground(startId, notification)

        val type = intent.getStringExtra("type")
        if (type == FINISH) stopSelf()
        val extra = intent.getSerializableExtra("actionList") ?: return super.onStartCommand(intent, flags, startId)
        val actionList = extra as ArrayList<Action>
        Thread {
            actionList.forEach {
                Log.e("ReplayService", "action: $it")
                ActionHelper.play(it)
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

}