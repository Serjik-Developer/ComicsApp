package com.example.texnostrelka_2025_otbor


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bitmap: Bitmap? = null
    private val canvas = Canvas()
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND // Сглаживание углов
        strokeCap = Paint.Cap.ROUND // Сглаживание концов линий
    }

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvas.setBitmap(this.bitmap)
    }
    fun setPaintColor(color: Int) {
        paint.color = color
    }

    fun setPaintStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Запоминаем начальные координаты
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                // Рисуем линию от предыдущей точки до текущей
                canvas.drawLine(lastX, lastY, event.x, event.y, paint)
                // Обновляем последние координаты
                lastX = event.x
                lastY = event.y
                // Перерисовываем View
                invalidate()
            }
        }
        return true
    }
}