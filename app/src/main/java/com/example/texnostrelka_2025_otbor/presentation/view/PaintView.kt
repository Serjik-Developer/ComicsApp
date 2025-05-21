package com.example.texnostrelka_2025_otbor.presentation.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.RectF
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.example.texnostrelka_2025_otbor.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.LinkedList
import java.util.Queue

class PaintView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val currentPath = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private var isEditTextCloudMode = false // Режим редактирования текстовых облаков
    private val boundaryColors = listOf(
        Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.rgb(128,0,128), Color.CYAN,
        Color.rgb(255, 165, 0), Color.rgb(255, 192, 203), Color.rgb(128, 128, 128), Color.rgb(71, 37, 0))
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
    private val textClouds = mutableListOf<TextCloud>() // Список текстовых облаков
    private var isMoveTextCloudMode = false // Режим перемещения текстовых облаков

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

    // Класс для хранения информации о текстовом облаке
    data class TextCloud(
        var text: String,
        var x: Float,
        var y: Float,
        var width: Float = 200f, // Ширина облачка по умолчанию
        var height: Float = 100f, // Высота облачка по умолчанию
        var isMoving: Boolean = false,
        var startX: Float = x,
        var startY: Float = y
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
            image.x = startX
            image.y = startY
        }

        override fun redo() {
            image.x = endX
            image.y = endY
        }
    }

    // Класс для действия добавления текстового облака
    private inner class AddTextCloudAction(private val textCloud: TextCloud) : Action {
        override fun undo() {
            textClouds.remove(textCloud)
        }

        override fun redo() {
            textClouds.add(textCloud)
        }
    }

    // Класс для действия перемещения текстового облака
    private inner class MoveTextCloudAction(
        private val textCloud: TextCloud,
        private val startX: Float,
        private val startY: Float,
        private val endX: Float,
        private val endY: Float
    ) : Action {
        override fun undo() {
            textCloud.x = startX
            textCloud.y = startY
        }

        override fun redo() {
            textCloud.x = endX
            textCloud.y = endY
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
    fun setMoveImageMode(enabled: Boolean) {
        isMoveImageMode = enabled
    }

    fun setMoveTextCloudMode(enabled: Boolean) {
        isMoveTextCloudMode = enabled
    }

    fun addTextCloud(text: String, x: Float, y: Float, width: Float = 200f, height: Float = 100f) {
        val textCloud = TextCloud(text, x, y, width, height)
        textClouds.add(textCloud)
        undoStack.add(AddTextCloudAction(textCloud))
        redoStack.clear()
        invalidate()
    }
    fun setEditTextCloudMode(enabled: Boolean) {
        isEditTextCloudMode = enabled
        isMoveTextCloudMode = false // Отключаем режим перемещения, если он был активен
        isMoveImageMode = false // Отключаем режим перемещения изображений
        isPanMode = false // Отключаем режим панорамирования
    }
    fun clearCanvas() {
        paths.clear()
        images.clear()
        textClouds.clear()
        fillBitmap?.eraseColor(Color.TRANSPARENT)
        undoStack.clear()
        redoStack.clear()
        redrawCanvas()
    }
    private fun redrawCanvas() {
        canvas?.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        canvas?.drawColor(Color.WHITE)
        images.forEach { image ->
            canvas?.drawBitmap(image.bitmap, image.x, image.y, null)
        }
        paths.forEach { drawingPath ->
            canvas?.drawPath(drawingPath.path, drawingPath.paint)
        }
        textClouds.forEach { textCloud ->
            val cloudPath = createTextCloudPath(
                textCloud.text,
                textCloud.x,
                textCloud.y,
                textCloud.width,
                textCloud.height
            )
            val fillPaint = Paint().apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            canvas?.drawPath(cloudPath, fillPaint)
            val strokePaint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }
            canvas?.drawPath(cloudPath, strokePaint)
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
            }
            val textX = textCloud.x + 20f
            val textY = textCloud.y + 50f
            canvas?.drawText(textCloud.text, textX, textY, textPaint)
        }
        invalidate()
    }
    private fun createTextCloudPath(text: String, x: Float, y: Float, width: Float, height: Float): Path {
        val path = Path()
        val rectWidth = width
        val rectHeight = height
        val cornerRadius = 20f // Закругление углов

        // Основной прямоугольник
        path.addRoundRect(
            RectF(x, y, x + rectWidth, y + rectHeight),
            cornerRadius, cornerRadius,
            Path.Direction.CW
        )

        // Хвостик облачка (треугольник)
        val tailWidth = 30f
        val tailHeight = 30f
        val tailX = x + rectWidth / 2 - tailWidth / 2
        val tailY = y + rectHeight

        path.moveTo(tailX, tailY)
        path.lineTo(tailX + tailWidth / 2, tailY + tailHeight)
        path.lineTo(tailX + tailWidth, tailY)
        path.close()

        return path
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
                if (isEditTextCloudMode) {
                    // Поиск текстового облака, на которое нажали
                    textClouds.forEach { textCloud ->
                        if (x >= textCloud.x && x <= textCloud.x + textCloud.width &&
                            y >= textCloud.y && y <= textCloud.y + textCloud.height) {
                            // Показываем диалог для редактирования текста
                            showEditTextDialog(textCloud)
                            return true // Блокируем дальнейшую обработку касаний
                        }
                    }
                    return true // Блокируем дальнейшую обработку касаний, даже если текстовое облако не найдено
                } else if (isPanMode) {
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
                            image.startX = image.x
                            image.startY = image.y
                        }
                    }
                } else if (isMoveTextCloudMode) {
                    textClouds.forEach { textCloud ->
                        if (x >= textCloud.x && x <= textCloud.x + textCloud.width &&
                            y >= textCloud.y && y <= textCloud.y + textCloud.height) {
                            textCloud.isMoving = true
                            textCloud.startX = textCloud.x
                            textCloud.startY = textCloud.y
                        }
                    }
                } else if (!isEditTextCloudMode) { // Блокируем создание путей в режиме редактирования
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
                            image.x = x - image.bitmap.width / 2
                            image.y = y - image.bitmap.height / 2
                            redrawCanvas()
                        }
                    }
                } else if (isMoveTextCloudMode) {
                    textClouds.forEach { textCloud ->
                        if (textCloud.isMoving) {
                            textCloud.x = x - textCloud.width / 2
                            textCloud.y = y - textCloud.height / 2
                            redrawCanvas()
                        }
                    }
                } else if (!isFillMode && !isEditTextCloudMode) { // Блокируем создание путей в режиме редактирования
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
                            val endX = image.x
                            val endY = image.y
                            undoStack.add(MoveImageAction(image, image.startX, image.startY, endX, endY))
                            redoStack.clear()
                            image.isMoving = false
                        }
                    }
                    isMoveImageMode = false
                } else if (isMoveTextCloudMode) {
                    textClouds.forEach { textCloud ->
                        if (textCloud.isMoving) {
                            val endX = textCloud.x
                            val endY = textCloud.y
                            undoStack.add(MoveTextCloudAction(textCloud, textCloud.startX, textCloud.startY, endX, endY))
                            redoStack.clear()
                            textCloud.isMoving = false
                        }
                    }
                    isMoveTextCloudMode = false
                } else if (isFillMode) {
                    isFillMode = !isFillMode
                } else if (!isFillMode && !isEditTextCloudMode) { // Блокируем создание путей в режиме редактирования
                    val savedPaint = Paint(paint)
                    if (isEraserMode) {
                        savedPaint.color = Color.WHITE
                    }

                    val savedPath = Path(currentPath)
                    val drawingPath = DrawingPath(savedPath, savedPaint)
                    paths.add(drawingPath)

                    undoStack.add(DrawAction(savedPath, savedPaint))
                    redoStack.clear()
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
    private fun showEditTextDialog(textCloud: TextCloud) {
        val context = context

        // Создаем EditText с Material Design стилем
        val input = EditText(context).apply {
            setText(textCloud.text)
            hint = "Введите текст"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1) // Стиль текста

        }

        // Создаем Material диалог
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("Редактировать текст")
            .setView(input) // Передаем EditText
            .setPositiveButton("Сохранить") { _, _ ->
                val newText = input.text.toString()
                if (newText.isNotEmpty()) {
                    textCloud.text = newText

                    // Обновляем размеры облачка, если текст слишком длинный
                    val textPaint = Paint().apply {
                        textSize = 40f
                    }
                    val textWidth = textPaint.measureText(newText)
                    if (textWidth > textCloud.width) {
                        textCloud.width = textWidth + 40f // Добавляем отступы
                    }

                    invalidate()
                }
                isEditTextCloudMode = false // Сбрасываем режим редактирования
            }
            .setNegativeButton("Отмена") { _, _ ->
                isEditTextCloudMode = false // Сбрасываем режим редактирования
            }
            .create()

        // Настройка кнопок
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Применяем стили к кнопкам
            positiveButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
            negativeButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
        }

        dialog.show()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(panX, panY)
        canvas.scale(scaleFactor, scaleFactor)

        // Отрисовка изображений
        images.forEach { image ->
            canvas.drawBitmap(image.bitmap, image.x, image.y, null)
        }

        // Отрисовка заливки
        fillBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        // Отрисовка путей
        paths.forEach { drawingPath ->
            canvas.drawPath(drawingPath.path, drawingPath.paint)
        }

        // Отрисовка текстовых облачков
        textClouds.forEach { textCloud ->
            // Рисуем форму облачка
            val cloudPath = createTextCloudPath(
                textCloud.text,
                textCloud.x,
                textCloud.y,
                textCloud.width,
                textCloud.height
            )

            // Заливка облачка (белый цвет)
            val fillPaint = Paint().apply {
                color = Color.WHITE
                style = Paint.Style.FILL
            }
            canvas.drawPath(cloudPath, fillPaint)

            // Контур облачка (черный цвет)
            val strokePaint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 5f // Толщина контура
            }
            canvas.drawPath(cloudPath, strokePaint)

            // Рисуем текст
            val textPaint = Paint().apply {
                color = Color.BLACK // Цвет текста
                textSize = 40f
            }
            val textX = textCloud.x + 20f // Отступ от края облачка
            val textY = textCloud.y + 50f // Отступ от верха облачка
            canvas.drawText(textCloud.text, textX, textY, textPaint)
        }

        // Отрисовка текущего пути
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

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}