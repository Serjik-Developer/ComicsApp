package com.example.texnostrelka_2025_otbor.presentation.ui.viewnetwork

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityViewNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.ViewNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.pagenetwork.PageNetworkActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ViewNetworkActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityViewNetworkBinding
    private val viewModel: ViewNetworkViewModel by viewModels {
        ViewNetworkViewModelFactory(
            NetworkRepository(),
            PreferencesManager(this)
        )
    }
    private lateinit var comicsAdapter: ComicsNetworkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNetworkBinding.inflate(layoutInflater)
        comicsAdapter = ComicsNetworkAdapter(mutableListOf(), this)
        setContentView(binding.root)
        binding.RecyclerViewNetwork.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewNetwork.adapter = comicsAdapter
        viewModel.comics.observe( this, Observer { comics ->
            comicsAdapter.updateData(comics)
        })
        viewModel.error.observe(this) { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        }
        viewModel.fetchComics()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchComics()
    }

    override fun onItemClick(id: String) {
        val intent = Intent(this, PageNetworkActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
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