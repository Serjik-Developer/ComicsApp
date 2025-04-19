package com.example.texnostrelka_2025_otbor.presentation.ui.mycomics

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.presentation.ui.pagenetwork.PageNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.databinding.ActivityMyComicsBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.MyComicsViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.editnetwork.EditNetworkActivity
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
        binding.btnAddNewComicsNetwork.setOnClickListener {
            showAddComicsDialog()
        }
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
        viewModel.postSuccess.observe(this, Observer { success ->
            if(success) Toast.makeText(this, "Успешно добавлено!", Toast.LENGTH_LONG).show()
        })
        viewModel.refreshTrigger.observe(this, Observer { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.fetchComics()
                viewModel.resetRefreshTrigger()
            }
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
        startActivity(Intent(this, EditNetworkActivity::class.java).putExtra("COMICS-ID", id))
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }
    private fun showAddComicsDialog() {
        val inputName = EditText(this).apply {
            hint = "Введите название комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }

        val inputDesc = EditText(this).apply {
            hint = "Введите описание комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputName)
            addView(inputDesc)
        }
        val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Добавить комикс")
            .setView(container)
            .setPositiveButton("Сохранить") { _, _ ->
                val desc = inputDesc.text.toString()
                val name = inputName.text.toString()
                if (desc.isNotEmpty() && name.isNotEmpty()) {
                    viewModel.postComics(name, desc)
                }
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            positiveButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
            negativeButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
        }
        dialog.show()
    }
}