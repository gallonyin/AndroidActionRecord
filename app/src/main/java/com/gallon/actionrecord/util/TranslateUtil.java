package com.gallon.actionrecord.util;

import android.view.MotionEvent;

import com.gallon.actionrecord.model.ActionUnit;

import java.util.List;

/**
 * Created by Gallon2 on 2019/3/28.
 */

public class TranslateUtil {

    public static void restoreActionTime(List<ActionUnit> actionUnitList) {
        if (actionUnitList.isEmpty()) return;
        long doneTime = actionUnitList.get(0).getActionTime();
        for (ActionUnit actionUnit : actionUnitList) {
            actionUnit.setActionTime(actionUnit.getActionTime() - doneTime);
        }
    }

}
