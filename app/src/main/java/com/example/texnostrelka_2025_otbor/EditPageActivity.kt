package com.example.texnostrelka_2025_otbor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.factories.EditPageViewModelFactory
import com.example.texnostrelka_2025_otbor.models.ImageModel
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.models.PageWithImages
import com.example.texnostrelka_2025_otbor.models.PageWithImagesIds
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import com.example.texnostrelka_2025_otbor.viewmodels.EditPageViewModel

class EditPageActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var pageId: String
    private val viewModel : EditPageViewModel by viewModels {
        EditPageViewModelFactory(pageId, ComicsRepository(ComicsDatabase(this)))
    }
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
        pageId = pageIdIntent ?: pageWithImagesIds?.page?.pageId ?: throw IllegalArgumentException("Page ID is required")
        if (pageWithImagesIds != null) {
            renderImages(pageWithImagesIds)
        } else {
            val rows = intent.getIntExtra("ROWS_COUNT", 1)
            val columns = intent.getIntExtra("COLUMNS_COUNT", 1)
            createNewGrid(rows, columns)
        }

        viewModel.images.observe(this, Observer { images ->
            updateGridWithImages(images)
        })

        viewModel.pageWithImages.observe(this, Observer { pageWithImages ->
            renderImages(pageWithImages)
        })

        viewModel.fetchPageWithImages()
        viewModel.fetchImages()
    }

    private fun renderImages(pageWithImages: PageWithImagesIds) {
        val page = pageWithImages.page
        gridLayout.rowCount = page.rows
        gridLayout.columnCount = page.columns

        gridLayout.removeAllViews()

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
                    viewModel.fetchImages()
                } else {
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

    private fun updateGridWithImages(images: List<ImageModel>) {
        for (i in 0 until gridLayout.childCount) {
            val imageView = gridLayout.getChildAt(i) as ImageView
            if (i < images.size) {
                imageView.setImageBitmap(images[i].image)
            } else {
                imageView.setBackgroundResource(android.R.color.darker_gray)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPageWithImages()
    }
}