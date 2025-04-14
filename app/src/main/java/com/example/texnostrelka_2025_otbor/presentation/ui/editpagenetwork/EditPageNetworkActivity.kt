package com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditPageAdapter
import com.example.texnostrelka_2025_otbor.presentation.factory.EditPageNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener

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
        pageId = intent.getStringExtra("COMICS_ID")!!
    }

    override fun onItemClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onEditClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }
}