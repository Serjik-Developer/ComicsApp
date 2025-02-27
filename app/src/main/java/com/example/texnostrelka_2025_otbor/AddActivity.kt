package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase

class AddActivity : AppCompatActivity() {
    private lateinit var paintView: PaintView
    private lateinit var databaseHelper: ComicsDatabase
    private lateinit var pageId: String
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        databaseHelper = ComicsDatabase(this)
        paintView = findViewById(R.id.paintView)
        pageId = intent.getStringExtra("PAGE_ID")!!

        // Обработчики для кнопок
        //ЦВЕТА
        findViewById<Button>(R.id.colorBlack).setOnClickListener {
            paintView.setColor(Color.BLACK)
        }
        findViewById<Button>(R.id.colorRed).setOnClickListener {
            paintView.setColor(Color.RED)
        }
        findViewById<Button>(R.id.colorBlue).setOnClickListener {
            paintView.setColor(Color.BLUE)
        }
        findViewById<Button>(R.id.colorGreen).setOnClickListener {
            paintView.setColor(Color.GREEN)
        }
        findViewById<Button>(R.id.colorYellow).setOnClickListener {
            paintView.setColor(Color.YELLOW)
        }
        findViewById<Button>(R.id.colorPurple).setOnClickListener {
            paintView.setColor(Color.rgb(128,0,128))
        }
        findViewById<Button>(R.id.colorCyan).setOnClickListener {
            paintView.setColor(Color.CYAN)
        }
        findViewById<Button>(R.id.colorOrange).setOnClickListener {
            paintView.setColor(Color.rgb(255, 165, 0))
        }
        findViewById<Button>(R.id.colorPink).setOnClickListener {
            paintView.setColor(Color.rgb(255, 192, 203))
        }
        findViewById<Button>(R.id.colorGrey).setOnClickListener {
            paintView.setColor(Color.rgb(128, 128, 128))
        }
        findViewById<Button>(R.id.colorBrown).setOnClickListener {
            paintView.setColor(Color.rgb(71, 37, 0))
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
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            savePainting()
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
        findViewById<Button>(R.id.moveTextCloudButton).setOnClickListener {
            paintView.setMoveTextCloudMode(true)
        }
        findViewById<Button>(R.id.addTextCloudButton).setOnClickListener {
            paintView.addTextCloud("New Text", 100f, 100f, 200f, 100f)
        }
        findViewById<Button>(R.id.editTextCloudButton).setOnClickListener {
            paintView.setEditTextCloudMode(true)
        }
        findViewById<Button>(R.id.clearCanvasButton).setOnClickListener {
            paintView.clearCanvas()
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
    private fun savePainting() {
        val bitmap = paintView.getBitmap()
        val cellIndex = intent.getIntExtra("CELL_INDEX", -1)
        val imageId = intent.getStringExtra("IMAGE_ID") // Получаем ID изображения, если оно есть

        if (cellIndex != -1) {
            if (imageId != null) {
                // Если изображение существует, обновляем его
                databaseHelper.updatePainting(imageId, bitmap)
                Toast.makeText(this, "Painting updated in cell $cellIndex!", Toast.LENGTH_SHORT).show()
            } else {
                // Если изображение новое, добавляем его
                databaseHelper.insertPainting(bitmap, pageId, cellIndex)
                Toast.makeText(this, "Painting saved in cell $cellIndex!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error: Cell index not found!", Toast.LENGTH_SHORT).show()
        }

        // Обновляем EditPageActivity
        val intent = Intent(this, EditPageActivity::class.java).apply {
            putExtra("PAGE_ID", pageId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish() // Закрываем текущую активность
    }
}
