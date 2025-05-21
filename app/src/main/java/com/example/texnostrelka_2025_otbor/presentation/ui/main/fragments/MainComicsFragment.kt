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
import com.example.texnostrelka_2025_otbor.data.local.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.FragmentMainComicsBinding
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.edit.EditActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.view.ViewActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.viewmodels.MainViewModel
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComicsFragment : Fragment(), OnItemClickListener {
    private var _binding : FragmentMainComicsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels ()
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
            if(success) Toast.makeText(requireContext(), "Успешно отправлено!", Toast.LENGTH_LONG).show()
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (error == "Не авторизован.") {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), AuthContainerActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        }
    }

    private fun showAddComicsDialog() {
        val inputName = EditText(requireContext()).apply {
            hint = "Введите название комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1) // Стиль текста
        }

        val inputDesc = EditText(requireContext()).apply {
            hint = "Введите описание комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1) // Стиль текста
        }

// Создаем контейнер для EditText
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputName)
            addView(inputDesc)
        }

// Создаем Material диалог
        val dialog = MaterialAlertDialogBuilder(requireContext())
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
        val intent = Intent(requireContext(), ViewActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)

    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteComics(id)
    }

    override fun onEditClick(id: String) {
        val intent = Intent(requireContext(), EditActivity::class.java)
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}