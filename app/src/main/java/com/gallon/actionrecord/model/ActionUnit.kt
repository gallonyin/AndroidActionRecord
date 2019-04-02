package com.gallon.actionrecord.model

import java.io.Serializable

/**
 * Created by Gallon2 on 2019/3/27.
 */
class ActionUnit(var action: Int = 0, var actionTime: Long = 0L, var rawX: Float = 0F, var rawY: Float = 0F): Serializable {
    override fun toString(): String {
        return "ActionUnit(action=$action, actionTime=$actionTime, rawX=$rawX, rawY=$rawY)"
    }
}