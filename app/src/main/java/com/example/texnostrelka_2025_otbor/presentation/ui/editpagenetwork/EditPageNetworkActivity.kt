package com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditPageNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemEditPageNetworkClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.editimagenetwork.EditImageNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPageNetworkActivity : AppCompatActivity(), OnItemEditPageNetworkClickListener {
    private lateinit var binding: ActivityEditPageNetworkBinding
    private val viewModel : EditPageNetworkViewModel by viewModels()
    private lateinit var adapter: EditPageAdapter
    private var pageId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditPageNetworkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        adapter = EditPageAdapter(mutableListOf(), this)
        binding.RecyclerViewEditPageNetwork.apply {
            layoutManager = LinearLayoutManager(this@EditPageNetworkActivity)
            adapter = this@EditPageNetworkActivity.adapter
            setHasFixedSize(true)
        }
        binding.backBtn.setOnClickListener {
           finish()
        }
        pageId = intent.getStringExtra("PAGE-ID") ?: throw IllegalArgumentException("pageId is required")
        viewModel.refreshTrigger.observe(this, Observer { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.fetchPage(pageId)
                viewModel.resetRefreshTrigger()
            }
        })
        viewModel.page.observe(this, Observer { page ->
            Log.w("DATA", "Received page: ${page.pageId}, images: ${page.images?.size}")
            adapter.updateData(mutableListOf(page))
        })
        viewModel.success.observe(this, Observer { success ->
            success?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.error.observe(this, Observer{ error ->
            error?.let{
                if(it.toString() == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthContainerActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        })
        viewModel.fetchPage(pageId)
    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteImage(id)
    }

    override fun onEditClick(id: String) {
        startActivity(Intent(this, EditImageNetworkActivity::class.java).apply {
            putExtra("MODE", 2)
            putExtra("IMAGE-ID", id)
        })
    }

    override fun onAddClick(pageId: String, cellIndex: Int) {
        startActivity(Intent(this, EditImageNetworkActivity::class.java).apply {
            putExtra("MODE", 1)
            putExtra("PAGE-ID", pageId)
            putExtra("CELL-INDEX", cellIndex)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPage(pageId)
    }
}