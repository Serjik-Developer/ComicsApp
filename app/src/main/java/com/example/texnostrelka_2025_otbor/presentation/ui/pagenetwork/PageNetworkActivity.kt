package com.example.texnostrelka_2025_otbor.presentation.ui.pagenetwork

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.presentation.adapter.PageNetworkAdapter
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityViewNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.PageNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.AppData
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
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
        viewModel.pages.observe(this) { pages ->
            Log.d("PAGES_DEBUG", "Pages count: ${pages?.size}")
            pages?.forEach { page ->
                Log.d("PAGES_DEBUG", "Page ${page.number} has ${page.images?.size} images")
            }
            pageNetworkAdapter.updateData(pages ?: mutableListOf())
        }
        viewModel.error.observe(this, Observer { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
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