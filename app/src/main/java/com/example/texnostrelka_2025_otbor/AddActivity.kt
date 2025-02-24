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
    private val undoStack = mutableListOf<Action>() // Стек для отмены
    private val redoStack = mutableListOf<Action>() // Стек для возврата
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
    private val minScaleFactor = 1.0f
    private val maxScaleFactor = 3.0f
    private val defaultPanX = 0f
    private val defaultPanY = 0f
    private var maxPanX = 0f
    private var maxPanY = 0f
    private val images = mutableListOf<DraggableImage>()
    private var isMoveImageMode = false
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
        var isMoving: Boolean = false,
        var startX: Float = x, // Начальная позиция X
        var startY: Float = y  // Начальная позиция Y
    )
    // Интерфейс для действий, которые можно отменить и вернуть
    private interface Action {
        fun undo()
        fun redo()
    }

    // Класс для действия рисования линии
    private inner class DrawAction(private val path: Path, private val paint: Paint) : Action {
        private val savedPath = Path(path) // Копируем Path

        override fun undo() {
            paths.removeIf { it.path == path } // Удаляем путь из списка
        }

        override fun redo() {
            paths.add(DrawingPath(Path(savedPath), paint)) // Добавляем копию пути обратно
        }
    }

    // Класс для действия заливки
    private inner class FillAction(private val oldFillBitmap: Bitmap, private val newFillBitmap: Bitmap) : Action {
        override fun undo() {
            fillBitmap = oldFillBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        override fun redo() {
            fillBitmap = newFillBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
    }

    // Класс для действия добавления изображения
    private inner class AddImageAction(private val image: DraggableImage) : Action {
        override fun undo() {
            images.remove(image)
        }

        override fun redo() {
            images.add(image)
        }
    }

    // Класс для действия перемещения изображения
    private inner class MoveImageAction(
        private val image: DraggableImage,
        private val startX: Float, // Начальная позиция X
        private val startY: Float, // Начальная позиция Y
        private val endX: Float,   // Конечная позиция X
        private val endY: Float    // Конечная позиция Y
    ) : Action {
        override fun undo() {
            // Возвращаем изображение в начальную позицию
            image.x = startX
            image.y = startY
        }

        override fun redo() {
            // Перемещаем изображение в конечную позицию
            image.x = endX
            image.y = endY
        }
    }

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

            maxPanX = w * (scaleFactor - 1)
            maxPanY = h * (scaleFactor - 1)
        }
    }

    fun setColor(color: Int) {
        paint.color = color
        paint.xfermode = null
        isEraserMode = false
    }

    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }

    fun setFillMode() {
        isFillMode = !isFillMode
        paint.xfermode = null
        isEraserMode = false
    }

    fun setEraserMode() {
        isEraserMode = true
        paint.color = Color.WHITE
        paint.xfermode = null
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val action = undoStack.removeAt(undoStack.size - 1)
            action.undo()
            redoStack.add(action)
            redrawCanvas()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val action = redoStack.removeAt(redoStack.size - 1)
            action.redo()
            undoStack.add(action)
            redrawCanvas()
        }
    }

    fun zoomIn() {
        if (scaleFactor < maxScaleFactor) {
            scaleFactor *= 1.2f
            maxPanX = width * (scaleFactor - 1)
            maxPanY = height * (scaleFactor - 1)
            invalidate()
        }
    }

    fun zoomOut() {
        if (scaleFactor > minScaleFactor) {
            scaleFactor /= 1.2f
            maxPanX = width * (scaleFactor - 1)
            maxPanY = height * (scaleFactor - 1)
            invalidate()
        }
    }

    fun setPanMode() {
        isPanMode = !isPanMode
    }

    private fun redrawCanvas() {
        canvas?.drawColor(Color.WHITE)
        canvasBitmap?.let { canvas?.drawBitmap(it, 0f, 0f, null) }
        images.forEach { image ->
            canvas?.drawBitmap(image.bitmap, image.x, image.y, null)
        }
        paths.forEach { drawingPath ->
            canvas?.drawPath(drawingPath.path, drawingPath.paint)
        }
        invalidate()
    }

    private fun floodFill(startX: Int, startY: Int, fillColor: Int) {
        if (canvasBitmap == null || fillBitmap == null) return

        val x = startX
        val y = startY

        val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)
        canvasBitmap?.let { tempCanvas.drawBitmap(it, 0f, 0f, null) }

        val targetColor = tempBitmap.getPixel(x, y)
        if (targetColor == fillColor) return

        val queue: Queue<Point> = LinkedList()
        queue.add(Point(x, y))

        while (queue.isNotEmpty()) {
            val point = queue.remove()
            val currentX = point.x
            val currentY = point.y

            if (currentX < 0 || currentX >= tempBitmap.width || currentY < 0 || currentY >= tempBitmap.height) continue

            val currentColor = tempBitmap.getPixel(currentX, currentY)
            if (boundaryColors.contains(currentColor)) continue

            if (fillBitmap!!.getPixel(currentX, currentY) == fillColor) continue

            fillBitmap!!.setPixel(currentX, currentY, fillColor)
            queue.add(Point(currentX + 1, currentY))
            queue.add(Point(currentX - 1, currentY))
            queue.add(Point(currentX, currentY + 1))
            queue.add(Point(currentX, currentY - 1))
        }

        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x - panX) / scaleFactor
        val y = (event.y - panY) / scaleFactor

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isPanMode) {
                    lastTouchX = event.x
                    lastTouchY = event.y
                } else if (isFillMode) {
                    if (x >= 0 && x < width && y >= 0 && y < height) {
                        val oldFillBitmap = fillBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                        floodFill(x.toInt(), y.toInt(), paint.color)
                        val newFillBitmap = fillBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                        undoStack.add(FillAction(oldFillBitmap!!, newFillBitmap!!))
                        redoStack.clear()
                    }
                } else if (isMoveImageMode) {
                    images.forEach { image ->
                        if (x >= image.x && x <= image.x + image.bitmap.width &&
                            y >= image.y && y <= image.y + image.bitmap.height) {
                            image.isMoving = true
                            // Сохраняем начальную позицию изображения
                            image.startX = image.x
                            image.startY = image.y
                        }
                    }
                } else {
                    currentPath.reset()
                    currentPath.moveTo(x, y)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isPanMode) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    val newPanX = panX + dx
                    val newPanY = panY + dy

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
                    images.forEach { image ->
                        if (image.isMoving) {
                            // Обновляем позицию изображения
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
                if (isPanMode) {
                    isPanMode = !isPanMode
                } else if (isMoveImageMode) {
                    images.forEach { image ->
                        if (image.isMoving) {
                            // Сохраняем конечную позицию изображения
                            val endX = image.x
                            val endY = image.y

                            // Добавляем действие в стек отмены
                            undoStack.add(MoveImageAction(image, image.startX, image.startY, endX, endY))
                            redoStack.clear()
                            image.isMoving = false
                        }
                    }
                    isMoveImageMode = false
                } else if (isFillMode) {
                    isFillMode = !isFillMode
                } else if (!isFillMode) {
                    val savedPaint = Paint(paint)
                    if (isEraserMode) {
                        savedPaint.color = Color.WHITE
                    }

                    // Создаем копию текущего пути
                    val savedPath = Path(currentPath)

                    // Сохраняем путь в список
                    val drawingPath = DrawingPath(savedPath, savedPaint)
                    paths.add(drawingPath)

                    // Добавляем действие в стек отмены
                    undoStack.add(DrawAction(savedPath, savedPaint))
                    redoStack.clear()
                    // Рисуем путь на canvasBitmap
                    canvasBitmap?.let { bmp ->
                        val tempCanvas = Canvas(bmp)
                        tempCanvas.drawPath(savedPath, savedPaint)
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
        canvas.translate(panX, panY)
        canvas.scale(scaleFactor, scaleFactor)

        images.forEach { image ->
            canvas.drawBitmap(image.bitmap, image.x, image.y, null)
        }

        fillBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        paths.forEach { drawingPath ->
            canvas.drawPath(drawingPath.path, drawingPath.paint)
        }

        canvas.drawPath(currentPath, paint)
        canvas.restore()
    }

    fun addImageFromUri(uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        bitmap?.let {
            val scaledBitmap = Bitmap.createScaledBitmap(it, width / 2, height / 2, true)
            val image = DraggableImage(scaledBitmap, 0f, 0f)
            images.add(image)
            undoStack.add(AddImageAction(image))
            redoStack.clear()
            redrawCanvas()
        }
    }

    fun setMoveImageMode(enabled: Boolean) {
        isMoveImageMode = enabled
    }
}