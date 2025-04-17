package com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork

import android.content.Intent
import android.graphics.pdf.PdfDocument.Page
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.mapper.convertNetworkToModel
import com.example.texnostrelka_2025_otbor.data.remote.model.page.PageFromNetwork
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditPageBinding
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditPageNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter
import com.example.texnostrelka_2025_otbor.presentation.factory.EditPageNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.editimagenetwork.EditImageNetworkActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditPageNetworkActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityEditPageNetworkBinding
    private val viewModel : EditPageNetworkViewModel by viewModels() {
        EditPageNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
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

        pageId = intent.getStringExtra("PAGE-ID") ?: throw IllegalArgumentException("pageId is required")

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
                if(it.toString() == "Не авторизован") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        })
        viewModel.fetchPage(pageId)
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") {dialog,_, -> dialog.dismiss()}
            .show()
    }

    override fun onItemClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteImage(id)
    }

    override fun onEditClick(id: String) {
        startActivity(Intent(this, EditImageNetworkActivity::class.java).putExtra("IMAGE-ID", id))
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }
}