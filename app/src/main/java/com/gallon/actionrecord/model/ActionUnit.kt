package com.gallon.actionrecord.model

/**
 * Created by Gallon2 on 2019/3/27.
 */
class ActionUnit(var action: Int = 0, var actionTime: Long = 0L, var rawX: Float = 0F, var rawY: Float = 0F) {
    override fun toString(): String {
        return "ActionUnit(action=$action, actionTime=$actionTime, rawX=$rawX, rawY=$rawY)"
    }
}