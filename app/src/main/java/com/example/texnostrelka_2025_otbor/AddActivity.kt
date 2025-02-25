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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        databaseHelper = ComicsDatabase(this)
        paintView = findViewById(R.id.paintView)
        //TODO СДЕЛАТЬ ЗДЕСЬ INTENT ID КОМИКСА И КАКОЕ ЭТО ИЗОБРАЖЕНИЕ ПО СЧЕТУ(СТРАНИЦА ТИПО)(НУЖНО ДЛЯ СЕЙВА В БД)!!!

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
        databaseHelper.insertPainting(bitmap, 1, "TESTID")
        Toast.makeText(this, "Painting saved!", Toast.LENGTH_SHORT).show()
    }
}
