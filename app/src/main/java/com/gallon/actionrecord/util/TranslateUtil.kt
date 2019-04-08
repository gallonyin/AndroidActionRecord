package com.gallon.actionrecord.util

import android.os.SystemClock
import com.gallon.actionrecord.model.ActionUnit
import java.math.BigInteger

/**
 * Created by Gallon2 on 2019/4/5.
 */
object TranslateUtil {

    /**
     * 使用该动作时调用，插入事件使用时间
     * @param actionUnitList
     */
    fun refreshActionTime(actionUnitList: List<ActionUnit>) {
        if (actionUnitList.isEmpty()) return
        val doneTime = actionUnitList[0].actionTime
        val now = SystemClock.uptimeMillis()
        for (actionUnit in actionUnitList) {
            actionUnit.actionTime = actionUnit.actionTime - doneTime + now
        }
    }

    /**
     * 十六进制转换十进制
     */
    fun hexToDec(str: String?): String? {
        val strArr = str?.split(" ")
        if (strArr?.size == 2) {
            return BigInteger(strArr[0], 16).toString(10) + " " +
                    BigInteger(strArr[1], 16).toString(10)
        }
        if (strArr?.size == 3) {
            return BigInteger(strArr[0], 16).toString(10) + " " +
                    BigInteger(strArr[1], 16).toString(10) + " " +
                    BigInteger(strArr[2], 16).toString(10)
        }
        return null
    }

}