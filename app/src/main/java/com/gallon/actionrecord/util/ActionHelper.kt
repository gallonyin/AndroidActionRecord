package com.gallon.actionrecord.util

import android.annotation.SuppressLint
import android.hardware.input.InputManager
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.MotionEvent
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ShellUtils
import com.gallon.actionrecord.model.Action
import com.gallon.actionrecord.model.TouchMap

/**
 * Created by Gallon2 on 2019/3/31.
 */
object ActionHelper {

    private val tag = "ActionHelper"

    /**
     * 重放动作
     * NOTE: 需要在子线程执行
     */
    fun play(action: Action) {
        if (action.type == ACTION_TYPE_SWIPE) {
            TranslateUtil.refreshActionTime(action.actionUnitList!!)
            val inputManager = InputManager::class.java.getMethod("getInstance").invoke(null) as InputManager
            val injectInputEventMethod = InputManager::class.java.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)

            var deviceId = 0
            for (id in InputDevice.getDeviceIds()) {
                if (InputDevice.getDevice(id).supportsSource(InputDevice.SOURCE_TOUCHSCREEN)) {
                    deviceId = id
                    break
                }
            }

            for (actionUnit in action.actionUnitList) {
                val motionEvent = MotionEvent.obtain(actionUnit.actionTime, actionUnit.actionTime, actionUnit.action, actionUnit.rawX, actionUnit.rawY, PRESSURE, DEFAULT_SIZE,
                        DEFAULT_META_STATE, DEFAULT_PRECISION_X, DEFAULT_PRECISION_Y, deviceId, DEFAULT_EDGE_FLAGS)
                motionEvent.source = InputDevice.SOURCE_TOUCHSCREEN
                injectInputEventMethod.invoke(inputManager, motionEvent, 2)
            }
        } else if (action.type == ACTION_TYPE_DELAY) {
            //todo
        }
    }

    /**
     * 随机坐标单击
     */
    private fun randomPx() {
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        val x: Float
        val y: Float
        val mainRandom = Math.random()
        x = (Math.random() * (screenWidth - 50) + 25).toFloat()
        y = (Math.random() * (screenHeight - 50) + 25).toFloat()
        Log.e(tag, "commandResult : ints: $x $y")
        ShellUtils.execCmd("input tap $x $y", false)
    }

    /**
     * 随机坐标滑动(加盐)
     */
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

            val now = SystemClock.uptimeMillis()

            val index = (Math.random() * TouchMap.X.size).toInt()
            Log.e(tag, "index : " + index)

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