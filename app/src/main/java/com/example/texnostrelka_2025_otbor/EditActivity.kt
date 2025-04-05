package com.example.texnostrelka_2025_otbor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.ImageAdapter
import com.example.texnostrelka_2025_otbor.adapters.PagesAdapter
import com.example.texnostrelka_2025_otbor.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.factories.EditViewModelFactory
import com.example.texnostrelka_2025_otbor.interfaces.OnItemPageClickListener
import com.example.texnostrelka_2025_otbor.models.Page
import com.example.texnostrelka_2025_otbor.models.PageWithImages
import com.example.texnostrelka_2025_otbor.models.PageWithImagesIds
import com.example.texnostrelka_2025_otbor.objects.AppData
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import com.example.texnostrelka_2025_otbor.viewmodels.EditViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.UUID


class EditActivity : AppCompatActivity(), OnItemPageClickListener {
    private val database: ComicsDatabase = ComicsDatabase(this)
    private lateinit var comicsId: String
    private lateinit var pageAdapter: PagesAdapter
    private val viewModel: EditViewModel by viewModels {
        EditViewModelFactory(comicsId, ComicsRepository(database))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        comicsId = intent.getStringExtra("COMICS_ID") ?: AppData.comicsId
        Log.w("COMICS_ID", comicsId)
        val addPageButton = findViewById<ImageButton>(R.id.button)
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerViewPages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pageAdapter = PagesAdapter(this, mutableListOf(), this, ComicsRepository(database), lifecycleScope)
        recyclerView.adapter = pageAdapter

        viewModel.pages.observe(this, Observer { pages ->
            pageAdapter.updateData(pages)
        })
        addPageButton.setOnClickListener {
            showAddPageDialog()
        }
        findViewById<Button>(R.id.backButtonMain).setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

    }

    private fun showAddPageDialog() {
        val inputRows = EditText(this).apply {
            hint = "Введите количество ячеек в длину"
        }

        val inputColumns = EditText(this).apply {
            hint = "Введите количество ячеек в ширину"
        }

        MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Создать страницу")
            .setView(LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 20, 50, 20)
                addView(inputRows)
                addView(inputColumns)
            })
            .setPositiveButton("Сохранить") { _, _ ->
                val rows = inputRows.text.toString().toIntOrNull() ?: 1
                val columns = inputColumns.text.toString().toIntOrNull() ?: 1
                viewModel.addPage(rows, columns, pageAdapter.itemCount)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onEditClick(page: PageWithImages) {
        val imageIds = page.images.map { it.id!! }
        val pageWithImageIds = PageWithImagesIds(page.page, imageIds)
        AppData.comicsId = comicsId
        startActivity(
            Intent(this, EditPageActivity::class.java)
                .putExtra("PAGE_WITH_IMAGES", pageWithImageIds)
        )
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(pageId: String) {
        viewModel.deletePage(pageId)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        viewModel.fetchPages()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchPages()
    }
}