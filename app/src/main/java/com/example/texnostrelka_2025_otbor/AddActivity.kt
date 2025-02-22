package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
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

        // Обработчики для кнопок
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

        // Обработчик для кнопки добавления изображения
        findViewById<Button>(R.id.addImageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Обработчик для кнопки активации режима перемещения изображения
        findViewById<Button>(R.id.moveImageButton).setOnClickListener {
            paintView.setMoveImageMode(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            imageUri?.let {
                paintView.addImageFromUri(it)
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
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

    // Список для хранения изображений на холсте
    private val images = mutableListOf<DraggableImage>()

    // Режим перемещения изображения
    private var isMoveImageMode = false

    // Класс для хранения информации о пути и его параметрах
    private data class DrawingPath(
        val path: Path,
        val paint: Paint
    )

    // Класс для хранения информации об изображении и его координатах
    private data class DraggableImage(
        val bitmap: Bitmap,
        var x: Float,
        var y: Float,
        var isMoving: Boolean = false
    )

    init {
        post {
            canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            canvas = Canvas(canvasBitmap!!)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            canvas = Canvas(canvasBitmap!!)
        }
    }

    fun setColor(color: Int) {
        paint.color = color
        paint.xfermode = null // Сбросить режим
        isEraserMode = false
    }

    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }

    fun setFillMode() {
        isFillMode = !isFillMode
        paint.xfermode = null // Сбросить режим
        isEraserMode = false
    }

    fun setEraserMode() {
        isEraserMode = true
        paint.color = Color.WHITE // Используем белый цвет для стирания
        paint.xfermode = null // Сбросить режим
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
        canvas?.drawColor(Color.WHITE) // Очищаем холст перед перерисовкой
        canvasBitmap?.let { canvas?.drawBitmap(it, 0f, 0f, null) } // Восстанавливаем пиксели (заливку)

        // Затем рисуем изображения поверх
        images.forEach { image ->
            canvas?.drawBitmap(image.bitmap, image.x, image.y, null)
        }

        // Рисуем уже нарисованные пути
        paths.forEach { drawingPath ->
            canvas?.drawPath(drawingPath.path, drawingPath.paint)
        }

        invalidate()
    }

    private fun floodFill(startX: Int, startY: Int, fillColor: Int) {
        if (canvasBitmap == null) return

        // Создаем временный Bitmap, который будет содержать объединенные данные
        val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)

        // Рисуем canvasBitmap на временном Bitmap
        canvasBitmap?.let { tempCanvas.drawBitmap(it, 0f, 0f, null) }

        // Рисуем изображения на временном Bitmap
        images.forEach { image ->
            tempCanvas.drawBitmap(image.bitmap, image.x, image.y, null)
        }

        // Выполняем заливку на временном Bitmap
        val queue: Queue<Point> = LinkedList()
        val targetColor = tempBitmap.getPixel(startX, startY)

        if (targetColor == fillColor) return // Уже залито

        queue.add(Point(startX, startY))

        while (queue.isNotEmpty()) {
            val point = queue.remove()
            val x = point.x
            val y = point.y

            // Проверка выхода за границы
            if (x < 0 || x >= tempBitmap.width || y < 0 || y >= tempBitmap.height) continue
            if (tempBitmap.getPixel(x, y) != targetColor) continue

            tempBitmap.setPixel(x, y, fillColor)

            queue.add(Point(x + 1, y))
            queue.add(Point(x - 1, y))
            queue.add(Point(x, y + 1))
            queue.add(Point(x, y - 1))
        }

        // Обновляем canvasBitmap и изображения
        canvasBitmap = tempBitmap
        redrawCanvas()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isFillMode) {
                    floodFill(x.toInt(), y.toInt(), paint.color)
                } else if (isMoveImageMode) {
                    images.forEach { image ->
                        if (x >= image.x && x <= image.x + image.bitmap.width &&
                            y >= image.y && y <= image.y + image.bitmap.height) {
                            image.isMoving = true
                        }
                    }
                } else {
                    currentPath.reset()
                    currentPath.moveTo(x, y)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMoveImageMode) {
                    images.forEach { image ->
                        if (image.isMoving) {
                            image.x = x - image.bitmap.width / 2
                            image.y = y - image.bitmap.height / 2
                            redrawCanvas()
                        }
                    }
                } else if (!isFillMode) {
                    currentPath.lineTo(x, y)
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isMoveImageMode) {
                    images.forEach { it.isMoving = false }
                    isMoveImageMode = false
                } else if (!isFillMode) {
                    val savedPaint = Paint(paint)
                    if (isEraserMode) {
                        savedPaint.color = Color.WHITE // Используем белый цвет для стирания
                    }

                    // Сохранение пути в список
                    paths.add(DrawingPath(Path(currentPath), savedPaint))

                    // Теперь рисуем путь непосредственно на canvasBitmap
                    canvasBitmap?.let { bmp ->
                        val tempCanvas = Canvas(bmp)
                        tempCanvas.drawPath(currentPath, savedPaint)
                    }

                    currentPath.reset()
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Сначала рисуем изображения
        images.forEach { image ->
            canvas.drawBitmap(image.bitmap, image.x, image.y, null)
        }

        // Затем рисуем уже сохраненные пути
        paths.forEach { drawingPath ->
            canvas.drawPath(drawingPath.path, drawingPath.paint)
        }
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        // И поверх всего рисуем текущий путь
        canvas.drawPath(currentPath, paint)
    }

    fun addImageFromUri(uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        bitmap?.let {
            val scaledBitmap = Bitmap.createScaledBitmap(it, width / 2, height / 2, true) // Масштабируем изображение
            images.add(DraggableImage(scaledBitmap, 0f, 0f))
            redrawCanvas()
        }
    }

    fun setMoveImageMode(enabled: Boolean) {
        isMoveImageMode = enabled
    }
}