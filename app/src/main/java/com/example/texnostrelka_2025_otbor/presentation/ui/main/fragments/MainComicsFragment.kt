package com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.edit.EditActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.view.ViewActivity
import com.example.texnostrelka_2025_otbor.viewmodelslist.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainComicsFragment : Fragment(), OnItemClickListener {
    private var _binding : FragmentMainComicsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: ComicsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainComicsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ComicsAdapter(mutableListOf(), this)
        binding.rvKomiks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKomiks.adapter = adapter

        viewModel.comics.observe(viewLifecycleOwner) { comics ->
            adapter.updateData(comics)
        }

        binding.btnNew.setOnClickListener {
            showAddComicsDialog()
        }
        viewModel.postSucces.observe(viewLifecycleOwner) { success ->
            if(success) Toast.makeText(this, "Успешно отправлено!", Toast.LENGTH_LONG).show()
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (error == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        }
        binding.imageBtnNetwork.setOnClickListener {
            // Переключение на сетевой фрагмент
            (activity as? MainContainerActivity)?.switchToNetworkFragment()
        }
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