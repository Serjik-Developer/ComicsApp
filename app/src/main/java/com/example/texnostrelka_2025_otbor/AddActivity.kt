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

        // Обработчики для кнопок масштабирования
        findViewById<Button>(R.id.zoomInButton).setOnClickListener {
            paintView.zoomIn()
        }
        findViewById<Button>(R.id.zoomOutButton).setOnClickListener {
            paintView.zoomOut()
        }
        findViewById<Button>(R.id.panButton).setOnClickListener {
            paintView.setPanMode()
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
    private val boundaryColors = listOf(Color.BLACK, Color.RED, Color.BLUE)
    private val paths = mutableListOf<DrawingPath>() // Список всех нарисованных путей
    private val undoStack = mutableListOf<DrawingPath>() // Стек для отмены
    private val redoStack = mutableListOf<DrawingPath>() // Стек для возврата
    private var isEraserMode = false
    private var isFillMode = false // Режим заливки
    private var canvasBitmap: Bitmap? = null
    private var canvas: Canvas? = null
    private var scaleFactor = 1.0f
    private var panX = 0f
    private var panY = 0f
    private var isPanMode = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    // Минимальный и максимальный масштаб
    private val minScaleFactor = 1.0f
    private val maxScaleFactor = 3.0f

    // Стандартные границы холста
    private val defaultPanX = 0f
    private val defaultPanY = 0f
    private var maxPanX = 0f
    private var maxPanY = 0f
    // Список для хранения изображений на холсте
    private val images = mutableListOf<DraggableImage>()

    // Режим перемещения изображения
    private var isMoveImageMode = false

    // Отдельный слой для заливки
    private var fillBitmap: Bitmap? = null
    private var fillCanvas: Canvas? = null

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
            fillBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            fillCanvas = Canvas(fillBitmap!!)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            canvas = Canvas(canvasBitmap!!)
            fillBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            fillCanvas = Canvas(fillBitmap!!)

            // Инициализация границ перемещения
            maxPanX = w * (scaleFactor - 1)
            maxPanY = h * (scaleFactor - 1)
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
    fun zoomIn() {
        if (scaleFactor < maxScaleFactor) {
            scaleFactor *= 1.2f
            // Обновляем границы перемещения при увеличении масштаба
            maxPanX = width * (scaleFactor - 1)
            maxPanY = height * (scaleFactor - 1)
            invalidate()
        }
    }

    fun zoomOut() {
        if (scaleFactor > minScaleFactor) {
            scaleFactor /= 1.2f
            // Обновляем границы перемещения при уменьшении масштаба
            maxPanX = width * (scaleFactor - 1)
            maxPanY = height * (scaleFactor - 1)
            invalidate()
        }
    }

    fun setPanMode() {
        isPanMode = !isPanMode
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
        if (canvasBitmap == null || fillBitmap == null) return

        // Корректируем координаты с учетом смещения холста и масштаба
        val x = startX
        val y = startY


        // Создаем временный Bitmap, который будет содержать объединенные данные
        val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)

        // Рисуем canvasBitmap на временном Bitmap
        canvasBitmap?.let { tempCanvas.drawBitmap(it, 0f, 0f, null) }

        // Получаем целевой цвет из временного Bitmap
        val targetColor = tempBitmap.getPixel(x, y)
        if (targetColor == fillColor) return // Уже залито

        val queue: Queue<Point> = LinkedList()
        queue.add(Point(x, y))

        while (queue.isNotEmpty()) {
            val point = queue.remove()
            val currentX = point.x
            val currentY = point.y

            // Проверка выхода за границы
            if (currentX < 0 || currentX >= tempBitmap.width || currentY < 0 || currentY >= tempBitmap.height) continue

            // Проверка цвета текущего пикселя на временном Bitmap
            val currentColor = tempBitmap.getPixel(currentX, currentY)

            // Если текущий цвет является границей (находится в списке boundaryColors), пропускаем его
            if (boundaryColors.contains(currentColor)) continue

            // Проверка, чтобы не заливать уже залитые пиксели
            if (fillBitmap!!.getPixel(currentX, currentY) == fillColor) continue

            // Заливаем текущий пиксель на fillBitmap
            fillBitmap!!.setPixel(currentX, currentY, fillColor)

            // Добавляем соседние пиксели в очередь
            queue.add(Point(currentX + 1, currentY)) // Вправо
            queue.add(Point(currentX - 1, currentY)) // Влево
            queue.add(Point(currentX, currentY + 1)) // Вниз
            queue.add(Point(currentX, currentY - 1)) // Вверх
        }

        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Корректируем координаты с учетом смещения холста и масштаба
        val x = (event.x - panX) / scaleFactor
        val y = (event.y - panY) / scaleFactor

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isPanMode) {
                    // Запоминаем начальные координаты касания для перемещения холста
                    lastTouchX = event.x
                    lastTouchY = event.y
                } else if (isFillMode) {
                    // Проверяем, что координаты находятся в пределах холста
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        floodFill(x.toInt(), y.toInt(), paint.color)
                    }
                } else if (isMoveImageMode) {
                    images.forEach { image ->
                        if (x >= image.x && x <= image.x + image.bitmap.width &&
                            y >= image.y && y <= image.y + image.bitmap.height) {
                            image.isMoving = true
                        }
                    }
                } else {
                    // Начинаем новый путь с учетом смещения холста
                    currentPath.reset()
                    currentPath.moveTo(x, y)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isPanMode) {
                    // Перемещаем холст
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    // Ограничиваем перемещение холста
                    val newPanX = panX + dx
                    val newPanY = panY + dy

                    // Проверяем, чтобы не выйти за границы
                    if (newPanX >= -maxPanX && newPanX <= maxPanX) {
                        panX = newPanX
                    }
                    if (newPanY >= -maxPanY && newPanY <= maxPanY) {
                        panY = newPanY
                    }

                    lastTouchX = event.x
                    lastTouchY = event.y
                    invalidate()
                } else if (isMoveImageMode) {
                    // Перемещаем изображение
                    images.forEach { image ->
                        if (image.isMoving) {
                            image.x = x - image.bitmap.width / 2
                            image.y = y - image.bitmap.height / 2
                            redrawCanvas()
                        }
                    }
                } else if (!isFillMode) {
                    // Рисуем линию с учетом смещения холста
                    currentPath.lineTo(x, y)
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isPanMode) {
                    isPanMode = !isPanMode
                } else if (isMoveImageMode) {
                    images.forEach { it.isMoving = false }
                    isMoveImageMode = false
                } else if (isFillMode) {
                    isFillMode = !isFillMode
                } else if (!isFillMode) {
                    val savedPaint = Paint(paint)
                    if (isEraserMode) {
                        savedPaint.color = Color.WHITE // Используем белый цвет для стирания
                    }

                    // Сохранение пути в список
                    paths.add(DrawingPath(Path(currentPath), savedPaint))

                    // Рисуем путь на canvasBitmap с учетом смещения холста
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

        canvas.save()
        canvas.translate(panX, panY) // Применяем смещение холста
        canvas.scale(scaleFactor, scaleFactor) // Применяем масштабирование

        // Сначала рисуем изображения
        images.forEach { image ->
            canvas.drawBitmap(image.bitmap, image.x, image.y, null)
        }

        // Затем рисуем слой заливки
        fillBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        // Затем рисуем уже сохраненные пути
        paths.forEach { drawingPath ->
            canvas.drawPath(drawingPath.path, drawingPath.paint)
        }

        // И поверх всего рисуем текущий путь
        canvas.drawPath(currentPath, paint)

        canvas.restore()
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