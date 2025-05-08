package com.example.texnostrelka_2025_otbor.presentation.ui.editpage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.mapper.convertLocalToNetworkModel
import com.example.texnostrelka_2025_otbor.data.model.PageWithImagesIdsModel
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemEditPageNetworkClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.add.AddActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.edit.EditActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPageActivity : AppCompatActivity(), OnItemEditPageNetworkClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pageId: String
    private val viewModel: EditPageViewModel by viewModels()
    private lateinit var adapter: EditPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_page_network)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.RecyclerViewEditPageNetwork)
        adapter = EditPageAdapter(mutableListOf(), this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditPageActivity)
            adapter = this@EditPageActivity.adapter
            setHasFixedSize(true)
        }

        val btn = findViewById<Button>(R.id.backBtn)
        btn.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

        val pageWithImagesIdsModel = intent.getParcelableExtra<PageWithImagesIdsModel>("PAGE_WITH_IMAGES")
        val pageIdIntent = intent.getStringExtra("PAGE_ID")
        pageId = pageIdIntent ?: pageWithImagesIdsModel?.pageModel?.pageId ?: throw IllegalArgumentException("Page ID is required")

        viewModel.pageWithImages.observe(this, Observer { pageWithImages ->
            pageWithImages?.let {
                updateAdapter(it)
            }
        })

        viewModel.images.observe(this, Observer { images ->
            val page = convertLocalToNetworkModel(pageId, images)
            adapter.updateData(mutableListOf(page))
        })

        viewModel.fetchPageWithImages(pageId)
        viewModel.fetchImages(pageId)
    }

    private fun updateAdapter(pageWithImages: PageWithImagesIdsModel) {
        viewModel.fetchImages(pageId)
    }

    override fun onDeleteClick(id: String) {
    }

    override fun onEditClick(id: String) {
        val intent = Intent(this, AddActivity::class.java).apply {
            putExtra("IMAGE_ID", id)
            putExtra("PAGE_ID", pageId)
        }
        startActivity(intent)
    }

    override fun onAddClick(pageId: String, cellIndex: Int) {
        val intent = Intent(this, AddActivity::class.java).apply {
            putExtra("PAGE_ID", pageId)
            putExtra("CELL_INDEX", cellIndex)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPageWithImages(pageId)
    }
}