package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class AddActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var drawingView: DrawingView
    private lateinit var btnSelectImage: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        imageView = findViewById(R.id.imageView)
        drawingView = findViewById(R.id.drawingView)
        btnSelectImage = findViewById(R.id.btnSelectImage)

        btnSelectImage.setOnClickListener {
            openGallery()
        }

        // Перемещение изображения
        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Запоминаем начальные координаты касания
                    lastX = event.rawX
                    lastY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    // Вычисляем смещение
                    val deltaX = event.rawX - lastX
                    val deltaY = event.rawY - lastY

                    // Перемещаем ImageView
                    imageView.translationX += deltaX
                    imageView.translationY += deltaY

                    // Обновляем последние координаты
                    lastX = event.rawX
                    lastY = event.rawY

                }
            }
            true
        }
    }

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                imageView.setImageBitmap(bitmap)
                drawingView.setBitmap(bitmap)
                drawingView.visibility = View.VISIBLE

            }
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

}