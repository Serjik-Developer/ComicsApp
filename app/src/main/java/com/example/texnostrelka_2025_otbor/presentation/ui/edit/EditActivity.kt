package com.example.texnostrelka_2025_otbor.presentation.ui.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.presentation.adapter.PagesAdapter
import com.example.texnostrelka_2025_otbor.data.local.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.presentation.factory.EditViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemPageClickListener
import com.example.texnostrelka_2025_otbor.data.model.PageWithImagesModel
import com.example.texnostrelka_2025_otbor.data.model.PageWithImagesIdsModel
import com.example.texnostrelka_2025_otbor.AppData
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.editpage.EditPageActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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
        findViewById<Button>(R.id.backButtonMain).setOnClickListener { startActivity(Intent(this, MainContainerActivity::class.java)) }

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

    override fun onEditClick(page: PageWithImagesModel) {
        val imageIds = page.images.map { it.id!! }
        val pageWithImageIds = PageWithImagesIdsModel(page.pageModel, imageIds)
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