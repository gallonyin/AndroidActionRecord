package com.gallon.actionrecord.model

import java.io.Serializable

/**
 * Created by Gallon2 on 2019/3/28.
 */
class Action(val type: Int, val actionUnitList: MutableList<ActionUnit>?, val delay: Long?): Serializable {
    override fun toString(): String {
        return "Action(type=$type, actionUnitList=$actionUnitList, delay=$delay)"
    }
}