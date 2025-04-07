package com.example.texnostrelka_2025_otbor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.adapters.PageNetworkAdapter
import com.example.texnostrelka_2025_otbor.database.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityViewNetworkBinding
import com.example.texnostrelka_2025_otbor.factories.PageNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.objects.AppData
import com.example.texnostrelka_2025_otbor.repositories.ComicsRepository
import com.example.texnostrelka_2025_otbor.repositories.NetworkRepository
import com.example.texnostrelka_2025_otbor.viewmodels.PageNetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PageNetworkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewNetworkBinding
    private lateinit var pageNetworkAdapter: PageNetworkAdapter
    private lateinit var comicsId: String
    private val viewModel: PageNetworkViewModel by viewModels {
        PageNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNetworkAdapter = PageNetworkAdapter(mutableListOf(), this)
        binding = ActivityViewNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("COMICS_ID ARE REQUIRED")
        AppData.comicsNetworkId = comicsId
        Log.w("COMICS_ID", comicsId)
        binding.RecyclerViewNetwork.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewNetwork.adapter = pageNetworkAdapter
        viewModel.pages.observe(this, Observer { pages ->
            pageNetworkAdapter.updateData(pages)
        })
        viewModel.error.observe(this, Observer { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
                else {
                    showErrorDialog(it)
                }
            }
        })
        viewModel.fetchPages(comicsId)
    }
    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}