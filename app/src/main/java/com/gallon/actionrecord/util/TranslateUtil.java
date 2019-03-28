package com.gallon.actionrecord.util;

import android.os.SystemClock;
import android.view.MotionEvent;

import com.gallon.actionrecord.model.ActionUnit;

import java.util.List;

/**
 * Created by Gallon2 on 2019/3/28.
 */

public class TranslateUtil {

    /**
     * 使用该动作时调用，插入事件使用时间
     * @param actionUnitList
     */
    public static void refreshActionTime(List<ActionUnit> actionUnitList) {
        if (actionUnitList.isEmpty()) return;
        long doneTime = actionUnitList.get(0).getActionTime();
        long now = SystemClock.uptimeMillis();
        for (ActionUnit actionUnit : actionUnitList) {
            actionUnit.setActionTime(actionUnit.getActionTime() - doneTime + now);
        }
    }

}
