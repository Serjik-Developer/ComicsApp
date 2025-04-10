package com.example.texnostrelka_2025_otbor.presentation.ui.editnetwork

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.factory.EditNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.ui.mycomics.MyComicsActivity

class EditNetworkActivity: AppCompatActivity() {
    private lateinit var adapter: EditNetworkAdapter
    private val viewModel: EditNetworkViewModel by viewModels {
        EditNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    private lateinit var binding: ActivityEditNetworkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditNetworkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        adapter = EditNetworkAdapter()
        binding.buttonAddNetworkPage.setOnClickListener {
            //TODO ADD DIALOG
        }
        binding.backButtonMainNetwork.setOnClickListener {
            startActivity(Intent(this, MyComicsActivity::class.java))
        }
        binding.RecyclerViewNetworkPages.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewNetworkPages.adapter = adapter
    }
}