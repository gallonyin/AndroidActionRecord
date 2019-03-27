package com.gallon.actionrecord

import android.hardware.input.InputManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.MotionEvent
import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ShellUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.gallon.actionrecord.model.ActionUnit
import com.gallon.actionrecord.model.TouchMap
import com.gallon.actionrecord.util.ACTION_IDLE
import com.gallon.actionrecord.util.ACTION_RECORD
import com.gallon.actionrecord.util.TranslateUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "Main2Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.init(this.application)

        initListener()
    }

    private var actionTime = 0L

    private var state = ACTION_IDLE

    private fun initListener() {
        bt_1.setOnClickListener {
            great()
        }
        bt_record.setOnClickListener {
            state = ACTION_RECORD
            bt_1.visibility = View.GONE
            bt_record.visibility = View.GONE
        }
        val unitList = ArrayList<ActionUnit>()
        ll_bg.setOnTouchListener { v, event ->
            if (state == ACTION_IDLE) return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    println("down")
                    val unit = ActionUnit().apply {
                        action = event.action
                        actionTime = System.currentTimeMillis()
                        rawX = event.rawX
                        rawY = event.rawY
                    }
                    unitList.add(unit)
                }
                MotionEvent.ACTION_MOVE -> {
                    println("move")
                    val unit = ActionUnit().apply {
                        action = event.action
                        actionTime = System.currentTimeMillis()
                        rawX = event.rawX
                        rawY = event.rawY
                    }
                    unitList.add(unit)
                }
                MotionEvent.ACTION_UP -> {
                    println("up")
                    val unit = ActionUnit().apply {
                        action = event.action
                        actionTime = System.currentTimeMillis()
                        rawX = event.rawX
                        rawY = event.rawY
                    }
                    unitList.add(unit)
                    if (unitList.count { it.action == MotionEvent.ACTION_DOWN } != 1) {
                        ToastUtils.showLong("该动作录制失败，请重试")
                        tv_action.text = ""
                    } else {
                        ToastUtils.showLong("该动作录制成功")
                        TranslateUtil.restoreActionTime(unitList)
                        tv_action.text = unitList.joinToString(",")
                    }
                    unitList.clear()
                    state = ACTION_IDLE
                    bt_1.visibility = View.VISIBLE
                    bt_record.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_CANCEL -> {
                    unitList.clear()
                    state = ACTION_IDLE
                    bt_1.visibility = View.VISIBLE
                    bt_record.visibility = View.VISIBLE
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun great() {
        object : Thread() {
            override fun run() {
//                randomPx()
//                randomSwipe()
                ShellUtils.execCmd("am start --user 0 com.tencent.mm/.ui.LauncherUI", false)
            }
        }.start()
    }

    private fun randomPx() {
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        val x: Float
        val y: Float
        val mainRandom = Math.random()
        x = (Math.random() * (screenWidth - 50) + 25).toFloat()
        y = (Math.random() * (screenHeight - 50) + 25).toFloat()
        Log.e(TAG, "commandResult : ints: $x $y")
        ShellUtils.execCmd("input tap $x $y", false)
    }

    private fun randomSwipe() {
        try {
            val inputManager = InputManager::class.java.getMethod("getInstance").invoke(null) as InputManager
            val injectInputEventMethod = InputManager::class.java.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)

            var deviceId = 0
            for (id in InputDevice.getDeviceIds()) {
                if (InputDevice.getDevice(id).supportsSource(InputDevice.SOURCE_TOUCHSCREEN)) {
                    deviceId = id
                    break
                }
            }

            val DEFAULT_SIZE = 1.0f
            val DEFAULT_META_STATE = 0
            val DEFAULT_PRECISION_X = 1.0f
            val DEFAULT_PRECISION_Y = 1.0f
            val PRESSURE = 1.0f
            val DEFAULT_EDGE_FLAGS = 0
            val now = SystemClock.uptimeMillis()

            val index = (Math.random() * TouchMap.X.size).toInt()
            Log.e(TAG, "index : " + index)

            val x = TouchMap.X[index] as FloatArray
            val y = TouchMap.Y[index] as FloatArray
            var len = (x.size * 0.7 + x.size.toDouble() * 0.3 * Math.random()).toInt()
            if (len > x.size) len = x.size
            val salt = (Math.random() * 20 - 10).toFloat()

            val eventDown = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x[0] + salt, y[0] + salt, PRESSURE, DEFAULT_SIZE,
                    DEFAULT_META_STATE, DEFAULT_PRECISION_X, DEFAULT_PRECISION_Y, deviceId, DEFAULT_EDGE_FLAGS)
            eventDown.source = InputDevice.SOURCE_TOUCHSCREEN
            injectInputEventMethod.invoke(inputManager, eventDown, 2)

            for (i in 1 until len - 1) {
                val eventMove = MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, x[i] + salt, y[i] + salt, PRESSURE, DEFAULT_SIZE,
                        DEFAULT_META_STATE, DEFAULT_PRECISION_X, DEFAULT_PRECISION_Y, deviceId, DEFAULT_EDGE_FLAGS)
                eventMove.source = InputDevice.SOURCE_TOUCHSCREEN
                injectInputEventMethod.invoke(inputManager, eventMove, 2)
            }

            val eventUp = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, x[len - 1] + salt, y[len - 1] + salt, PRESSURE, DEFAULT_SIZE,
                    DEFAULT_META_STATE, DEFAULT_PRECISION_X, DEFAULT_PRECISION_Y, deviceId, DEFAULT_EDGE_FLAGS)
            eventUp.source = InputDevice.SOURCE_TOUCHSCREEN
            injectInputEventMethod.invoke(inputManager, eventUp, 2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
