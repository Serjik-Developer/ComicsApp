package com.example.texnostrelka_2025_otbor.presentation.ui.editnetwork

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.factory.EditNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.mycomics.MyComicsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditNetworkActivity: AppCompatActivity() {
    private lateinit var adapter: EditNetworkAdapter
    private val viewModel: EditNetworkViewModel by viewModels {
        EditNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    private lateinit var binding: ActivityEditNetworkBinding
    private var comicsId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditNetworkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("comicsId is required")
        adapter = EditNetworkAdapter()
        binding.buttonAddNetworkPage.setOnClickListener {
            //TODO ADD DIALOG
        }
        binding.backButtonMainNetwork.setOnClickListener {
            startActivity(Intent(this, MyComicsActivity::class.java))
        }
        binding.RecyclerViewNetworkPages.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewNetworkPages.adapter = adapter
        viewModel.error.observe(this, Observer{ error->
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
        viewModel.success.observe(this, Observer { success ->
            if(success) Toast.makeText(this, "Успешно добавлено!", Toast.LENGTH_LONG).show()
        })
    }

    private fun showAddPageDialog() {
        val inputRows = EditText(this).apply {
            hint = "Введите количество ячеек в длину"
        }

        val inputColumns = EditText(this).apply {
            hint = "Введите количество ячеек в ширину"
        }

        MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Создать страницу")
            .setView(LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 20, 50, 20)
                addView(inputRows)
                addView(inputColumns)
            })
            .setPositiveButton("Сохранить") { _, _ ->
                val rows = inputRows.text.toString().toIntOrNull() ?: 1
                val columns = inputColumns.text.toString().toIntOrNull() ?: 1
                viewModel.addPage(comicsId, rows, columns)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") {dialog,_, -> dialog.dismiss()}
            .show()
    }
}