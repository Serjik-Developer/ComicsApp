package com.example.texnostrelka_2025_otbor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import java.util.LinkedList
import java.util.Queue


class AddActivity : AppCompatActivity() {
    private lateinit var paintView: PaintView
//Сегодня 14 февраля, всемирный день программиста и мой день рождение(мне судьбой решено стать программистом) вообще этим коммитом хотел просто всех программистов поздравить с нашим проффесиональным праздником :)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

    paintView = findViewById(R.id.paintView)

    findViewById<Button>(R.id.colorBlack).setOnClickListener {
        paintView.setColor(Color.BLACK)
    }
    findViewById<Button>(R.id.colorRed).setOnClickListener {
        paintView.setColor(Color.RED)
    }
    findViewById<Button>(R.id.colorBlue).setOnClickListener {
        paintView.setColor(Color.BLUE)
    }
    findViewById<Button>(R.id.fillButton).setOnClickListener {
        paintView.setFillMode()
    }
    findViewById<Button>(R.id.eraserButton).setOnClickListener {
        paintView.setEraserMode()
    }

    findViewById<SeekBar>(R.id.strokeWidthSeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            paintView.setStrokeWidth(progress.toFloat())
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
}
}

class PaintView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val path = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var canvasBitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var isFillMode = false
    private var isEraserMode = false

    init {
        post {
            canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            canvas = Canvas(canvasBitmap!!)
        }
    }

    fun setColor(color: Int) {
        paint.color = color
        isEraserMode = false
    }

    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }

    fun setFillMode() {
        isFillMode = true
    }

    fun setEraserMode() {
        isEraserMode = true
        paint.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        paths.forEach { (p, paint) ->
            canvas.drawPath(p, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isFillMode) {
            floodFill(event.x.toInt(), event.y.toInt(), paint.color)
            isFillMode = false
            invalidate()
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                val newPaint = Paint(paint)
                if (isEraserMode) {
                    newPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
                paths.add(Pair(Path(path), newPaint))
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                paths.add(Pair(Path(path), Paint(paint)))
                canvas?.drawPath(path, paint)
                path.reset()
            }
        }
        return true
    }

    private fun floodFill(x: Int, y: Int, newColor: Int) {
        val bitmap = canvasBitmap ?: return
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val targetColor = pixels[y * width + x]
        if (targetColor == newColor) return

        val queue: Queue<Point> = LinkedList()
        queue.add(Point(x, y))

        while (queue.isNotEmpty()) {
            val point = queue.remove()
            val px = point.x
            val py = point.y

            if (px < 0 || py < 0 || px >= width || py >= height) continue
            if (pixels[py * width + px] != targetColor) continue

            pixels[py * width + px] = newColor
            queue.add(Point(px + 1, py))
            queue.add(Point(px - 1, py))
            queue.add(Point(px, py + 1))
            queue.add(Point(px, py - 1))
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        invalidate()
    }
}