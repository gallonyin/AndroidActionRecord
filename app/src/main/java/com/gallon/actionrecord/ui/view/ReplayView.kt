package com.gallon.actionrecord.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.graphics.PointF
import android.util.AttributeSet
import java.util.jar.Attributes


/**
 * Created by Gallon2 on 2019/3/29.
 */
class ReplayView: View {

    private val graphics = ArrayList<PointF>()

    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 3F
    }

    public fun clearDraw() {
        graphics.clear()
        invalidate()
    }

    public fun refreshView(event: MotionEvent) {
        graphics.add(PointF(event.x, event.y))
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        graphics.add(PointF(event.x, event.y))
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (graphics.isEmpty()) return
        var lastPointF: PointF? = null
        graphics.forEachIndexed { index, pointF ->
            if (index == 0) {
                canvas.drawPoint(pointF.x, pointF.y, paint)
            } else if (lastPointF != null) {
                canvas.drawLine(lastPointF!!.x, lastPointF!!.y, pointF.x, pointF.y, paint)
            }
            lastPointF = pointF
        }
    }

}