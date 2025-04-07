package com.example.texnostrelka_2025_otbor.presentation.ui.main


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import com.example.texnostrelka_2025_otbor.presentation.ui.view.ViewActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.viewnetwork.ViewNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsAdapter
import com.example.texnostrelka_2025_otbor.data.local.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.presentation.factory.MainViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.edit.EditActivity
import com.example.texnostrelka_2025_otbor.viewmodelslist.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var comicsAdapter: ComicsAdapter
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(ComicsRepository(ComicsDatabase(this)), NetworkRepository(),
            PreferencesManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        comicsAdapter = ComicsAdapter(mutableListOf(), this)
        val add_btn = findViewById<ImageButton>(R.id.btn_new)
        val recycler_view = findViewById<RecyclerView>(R.id.rv_komiks)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = comicsAdapter

        viewModel.comics.observe(this, Observer { comics ->
            comicsAdapter.updateData(comics)
        })
        viewModel.postSucces.observe(this, Observer { success ->
            if(success) Toast.makeText(this, "Успешно отправлено!", Toast.LENGTH_LONG).show()
        })
        viewModel.errorMessage.observe(this, Observer { error ->
            error?.let {
                if (error == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        })
        add_btn.setOnClickListener {
            showAddComicsDialog()
        }
        findViewById<ImageButton>(R.id.imageBtnNetwork).setOnClickListener { startActivity(Intent(this,
            ViewNetworkActivity::class.java)) }
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showAddComicsDialog() {
        val inputName = EditText(this).apply {
            hint = "Введите название комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1) // Стиль текста
        }

        val inputDesc = EditText(this).apply {
            hint = "Введите описание комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1) // Стиль текста
        }

// Создаем контейнер для EditText
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputName)
            addView(inputDesc)
        }

// Создаем Material диалог
        val dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Добавить комикс")
            .setView(container) // Передаем контейнер с EditText
            .setPositiveButton("Сохранить") { _, _ ->
                val desc = inputDesc.text.toString()
                val name = inputName.text.toString()
                if (desc.isNotEmpty() && name.isNotEmpty()) {
                    viewModel.addComics(name, desc)
                }
            }
            .setNegativeButton("Отмена") { _, _ -> }
            .create()

// Настройка кнопок
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Применяем стили к кнопкам
            positiveButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
            negativeButton.setTextAppearance(R.style.TextAppearance_MaterialComponents_Button)
        }

        dialog.show()
    }

    override fun onItemClick(id: String) {
        val intent = Intent(this, ViewActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)

    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteComics(id)
    }

    override fun onEditClick(id: String) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
    }

    override fun onSendClick(id: String) {
        viewModel.postComics(id)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchComics()
    }
}