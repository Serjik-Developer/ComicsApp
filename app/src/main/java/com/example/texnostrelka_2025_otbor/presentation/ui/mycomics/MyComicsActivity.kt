package com.example.texnostrelka_2025_otbor.presentation.ui.mycomics

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.presentation.ui.pagenetwork.PageNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityMyComicsBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.MyComicsViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyComicsActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivityMyComicsBinding
    private val viewModel : MyComicsViewModel by viewModels {
        MyComicsViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    private lateinit var comicsNetworkAdapter: ComicsNetworkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyComicsBinding.inflate(layoutInflater)
        comicsNetworkAdapter = ComicsNetworkAdapter(mutableListOf(), this, true)
        setContentView(binding.root)
        binding.RecyclerViewMyComics.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewMyComics.adapter = comicsNetworkAdapter
        viewModel.comics.observe(this, Observer { comics ->
            comicsNetworkAdapter.updateData(comics)
        })
        viewModel.error.observe(this, Observer { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        })
        viewModel.deleteSuccess.observe(this, Observer { success ->
            if(success) Toast.makeText(this, "Успешно удалено!", Toast.LENGTH_LONG).show()
        })
        viewModel.fetchComics()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchComics()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onItemClick(id: String) {
        val intent = Intent(this, PageNetworkActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteComics(id)
    }

    override fun onEditClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }
}