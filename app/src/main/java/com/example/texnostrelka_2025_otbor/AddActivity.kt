package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
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
        findViewById<Button>(R.id.undoButton).setOnClickListener {
            paintView.undo()
        }

        findViewById<Button>(R.id.redoButton).setOnClickListener {
            paintView.redo()
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
    private val currentPath = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val paths = mutableListOf<DrawingPath>() // Список всех нарисованных путей
    private val undoStack = mutableListOf<DrawingPath>() // Стек для отмены
    private val redoStack = mutableListOf<DrawingPath>() // Стек для возврата
    private var isEraserMode = false
    private var isFillMode = false // Режим заливки
    private var canvasBitmap: Bitmap? = null
    private var canvas: Canvas? = null

    // Класс для хранения информации о пути и его параметрах
    private data class DrawingPath(
        val path: Path,
        val paint: Paint
    )

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
        isFillMode = !isFillMode
    }

    fun setEraserMode() {
        isEraserMode = true
        paint.color = Color.WHITE
    }

    // Метод для отмены последнего действия
    fun undo() {
        if (paths.isNotEmpty()) {
            val lastPath = paths.removeAt(paths.size - 1)
            undoStack.add(lastPath)
            redrawCanvas()
        }
    }

    // Метод для возврата отмененного действия
    fun redo() {
        if (undoStack.isNotEmpty()) {
            val lastUndoPath = undoStack.removeAt(undoStack.size - 1)
            paths.add(lastUndoPath)
            redrawCanvas()
        }
    }

    // Перерисовывает весь холст
    private fun redrawCanvas() {
        canvasBitmap?.eraseColor(Color.TRANSPARENT) // Очищаем холст
        paths.forEach { drawingPath ->
            canvas?.drawPath(drawingPath.path, drawingPath.paint)
        }
        invalidate()
    }

    private fun floodFill(startX: Float, startY: Float, fillColor: Int) {
        val startPixel = canvasBitmap?.getPixel(startX.toInt(), startY.toInt())
        if (startPixel == fillColor) return // Avoid refilling the already filled area

        val queue: Queue<Point> = LinkedList()
        val targetColor = startPixel ?: return // Ensure a valid start color

        queue.add(Point(startX.toInt(), startY.toInt()))

        while (queue.isNotEmpty()) {
            val point = queue.remove()
            val x = point.x
            val y = point.y

            // Check if pixel is within bounds and matches the target color
            if (x < 0 || x >= canvasBitmap?.width ?: 0 || y < 0 || y >= canvasBitmap?.height ?: 0) continue
            if (canvasBitmap?.getPixel(x, y) != targetColor) continue

            // Fill the pixel
            canvasBitmap?.setPixel(x, y, fillColor)

            // Add neighboring points to the queue
            queue.add(Point(x + 1, y))
            queue.add(Point(x - 1, y))
            queue.add(Point(x, y + 1))
            queue.add(Point(x, y - 1))
        }

        invalidate() // Refresh the canvas to show the filled area
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isFillMode) {
                    // Trigger flood fill if in fill mode

                    floodFill(x, y, paint.color)
                } else {
                    currentPath.reset()
                    currentPath.moveTo(x, y)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isFillMode) {
                    currentPath.lineTo(x, y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isFillMode) {
                    val savedPaint = Paint(paint)
                    if (isEraserMode) {
                        savedPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    }
                    paths.add(DrawingPath(Path(currentPath), savedPaint))
                    canvas?.drawPath(currentPath, savedPaint)
                    currentPath.reset()
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 0f, 0f, null)
        }
        canvas.drawPath(currentPath, paint)
    }
}
