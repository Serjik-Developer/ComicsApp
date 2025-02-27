package com.example.texnostrelka_2025_otbor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.models.PageWithImages
import com.example.texnostrelka_2025_otbor.models.PageWithImagesIds

class EditPageActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var pageId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gridLayout = findViewById(R.id.gridLayout)

        val btn = findViewById<Button>(R.id.backButton)
        btn.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
        val pageWithImagesIds = intent.getParcelableExtra<PageWithImagesIds>("PAGE_WITH_IMAGES")
        val pageIdIntent = intent.getStringExtra("PAGE_ID")
        if (pageIdIntent == null) {
            pageId = pageWithImagesIds!!.page.pageId
        }
        else {
            pageId = pageIdIntent.toString()
        }
        if (pageWithImagesIds != null) {
            // Отрисовка существующих изображений
            renderImages(pageWithImagesIds)
        } else {
            // Создание новой сетки
            val rows = intent.getIntExtra("ROWS_COUNT", 1)
            val columns = intent.getIntExtra("COLUMNS_COUNT", 1)
            createNewGrid(rows, columns)
        }


    }

    private fun renderImages(pageWithImages: PageWithImagesIds) {
        val page = pageWithImages.page
        gridLayout.rowCount = page.rows
        gridLayout.columnCount = page.columns

        // Очищаем GridLayout перед добавлением новых изображений
        gridLayout.removeAllViews()

        val database = ComicsDatabase(this)
        val imageIds = pageWithImages.imageIds

        // Создаем список всех ячеек
        for (i in 0 until page.rows * page.columns) {
            val imageView = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(i / page.columns, 1f)
                    columnSpec = GridLayout.spec(i % page.columns, 1f)
                }

                // Если изображение есть, загружаем его
                if (i < imageIds.size) {
                    val imageModel = database.getImageById(imageIds[i])
                    if (imageModel != null) {
                        setImageBitmap(imageModel.image)
                    }
                } else {
                    // Если изображения нет, добавляем серый фон
                    setBackgroundResource(android.R.color.darker_gray)
                }

                scaleType = ImageView.ScaleType.CENTER_CROP
                setOnClickListener {
                    val intent = Intent(this@EditPageActivity, AddActivity::class.java).apply {
                        putExtra("PAGE_ID", pageId)
                        putExtra("CELL_INDEX", i) // Передаем индекс ячейки

                        // Если изображение существует, передаем его ID
                        if (i < imageIds.size) {
                            putExtra("IMAGE_ID", imageIds[i])
                        }
                    }
                    startActivity(intent)
                }
            }
            gridLayout.addView(imageView)
        }
    }

    private fun createNewGrid(rows: Int, columns: Int) {
        // Очищаем GridLayout перед созданием новой сетки
        gridLayout.removeAllViews()

        gridLayout.rowCount = rows
        gridLayout.columnCount = columns

        for (i in 0 until rows * columns) {
            val imageView = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(i / columns, 1f)
                    columnSpec = GridLayout.spec(i % columns, 1f)
                }
                setBackgroundResource(android.R.color.darker_gray)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setOnClickListener {
                    val intent = Intent(this@EditPageActivity, AddActivity::class.java).putExtra("PAGE_ID", pageId)
                    startActivity(intent)
                }
            }
            gridLayout.addView(imageView)
        }
    }
    override fun onResume() {
        super.onResume()
        val pageWithImagesIds = intent.getParcelableExtra<PageWithImagesIds>("PAGE_WITH_IMAGES")
        val pageIdIntent = intent.getStringExtra("PAGE_ID")

        if (pageIdIntent == null && pageWithImagesIds != null) {
            pageId = pageWithImagesIds.page.pageId
            renderImages(pageWithImagesIds)
        } else if (pageIdIntent != null) {
            pageId = pageIdIntent
            val database = ComicsDatabase(this)
            val page = database.getMyPage(pageId).find { it.pageId == pageId }
            if (page != null) {
                val imageIds = database.getAllImagesOnPage(pageId).map { it.id!! }
                val pageWithImages = PageWithImagesIds(page, imageIds)
                renderImages(pageWithImages)
            }
        }
    }
}