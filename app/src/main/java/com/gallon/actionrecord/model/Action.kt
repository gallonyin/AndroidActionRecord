package com.gallon.actionrecord.model

import java.io.Serializable

/**
 * Created by Gallon2 on 2019/3/28.
 */
class Action(val actionUnitList: MutableList<ActionUnit>, val index: Int): Serializable {
    override fun toString(): String {
        return "Action(actionUnitList=$actionUnitList, index=$index)"
    }
}