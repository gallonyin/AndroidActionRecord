package com.gallon.actionrecord.model

/**
 * Created by Gallon2 on 2019/3/28.
 */
class Action(val actionUnitList: MutableList<ActionUnit>, val index: Int) {
    override fun toString(): String {
        return "Action(actionUnitList=$actionUnitList, index=$index)"
    }
}