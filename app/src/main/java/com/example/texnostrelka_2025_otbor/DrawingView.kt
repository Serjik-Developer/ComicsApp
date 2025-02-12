package com.example.myapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paths = mutableListOf<Path>()
    private val paints = mutableListOf<Paint>()
    private var currentPath = Path()
    private var currentPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
    }

    init {
        paints.add(currentPaint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in paths.indices) {
            canvas.drawPath(paths[i], paints[i])
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.moveTo(x, y)
                paths.add(currentPath)
                paints.add(currentPaint)
            }
            MotionEvent.ACTION_MOVE -> currentPath.lineTo(x, y)
            MotionEvent.ACTION_UP -> {
                currentPath = Path()
                currentPaint = Paint(currentPaint)
            }
        }
        invalidate()
        return true
    }

    fun setColor(color: Int) {
        currentPaint.color = color
    }

    fun setStrokeWidth(width: Float) {
        currentPaint.strokeWidth = width
    }

    fun clear() {
        paths.clear()
        paints.clear()
        invalidate()
    }
}