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
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.EditNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork.EditPageNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNetworkActivity: AppCompatActivity(), OnItemClickListener {
    private lateinit var adapter: EditNetworkAdapter
    private val viewModel: EditNetworkViewModel by viewModels ()
    private lateinit var binding: ActivityEditNetworkBinding
    private var comicsId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditNetworkBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        comicsId = intent.getStringExtra("COMICS-ID") ?: throw IllegalArgumentException("comicsId is required")
        adapter = EditNetworkAdapter(mutableListOf(), this, this)
        binding.buttonAddNetworkPage.setOnClickListener {
            showAddPageDialog()
        }
        binding.backButtonMainNetwork.setOnClickListener {
            startActivity(Intent(this, MainContainerActivity::class.java))
        }
        viewModel.refreshTrigger.observe(this, Observer { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.fetchPages(comicsId)
                viewModel.resetRefreshTrigger()
            }
        })
        binding.RecyclerViewNetworkPages.layoutManager = LinearLayoutManager(this)
        binding.RecyclerViewNetworkPages.adapter = adapter
        viewModel.pages.observe(this, Observer { pages ->
            adapter.updateData(pages)
        })
        viewModel.error.observe(this, Observer{ error ->
            error?.let{
                if(it.toString() == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        })
        viewModel.success.observe(this, Observer { success ->
            success?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.fetchPages(comicsId)
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

    override fun onItemClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteClick(id: String) {
        viewModel.deletePage(id)
    }

    override fun onEditClick(id: String) {
        startActivity(Intent(this, EditPageNetworkActivity::class.java).putExtra("PAGE-ID", id))
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }
    override fun onResume() {
        super.onResume()
        viewModel.fetchPages(comicsId)
    }
}