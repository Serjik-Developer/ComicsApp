package com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.mapper.convertNetworkToModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter
import com.example.texnostrelka_2025_otbor.presentation.factory.EditPageNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditPageNetworkActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityEditNetworkBinding
    private val viewModel : EditPageNetworkViewModel by viewModels() {
        EditPageNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    private lateinit var adapter: EditPageAdapter
    private var pageId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNetworkBinding.inflate(layoutInflater)
        adapter = EditPageAdapter(listOf(), this)
        pageId = intent.getStringExtra("PAGE-ID") ?: throw IllegalArgumentException("pageId is required")
        viewModel.page.observe(this, Observer { page ->
            val imageList = convertNetworkToModel(page.images, pageId)
            adapter.updateData(imageList)
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
        TODO("Not yet implemented")
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }
}