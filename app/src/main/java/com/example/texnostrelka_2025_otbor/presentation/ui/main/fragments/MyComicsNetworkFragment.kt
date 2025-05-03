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
import com.example.texnostrelka_2025_otbor.databinding.FragmentMyComicsBinding
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemComicsListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthContainerActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.editnetwork.EditNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.viewmodels.MyComicsViewModel
import com.example.texnostrelka_2025_otbor.presentation.ui.comicnetwork.ComicNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyComicsNetworkFragment : Fragment(), OnItemComicsListener {
    private var _binding : FragmentMyComicsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyComicsViewModel by activityViewModels()
    private lateinit var comicsNetworkAdapter: ComicsNetworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyComicsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comicsNetworkAdapter = ComicsNetworkAdapter(mutableListOf(), this, true)
        binding.RecyclerViewMyComics.layoutManager = LinearLayoutManager(requireContext())
        binding.RecyclerViewMyComics.adapter = comicsNetworkAdapter
        viewModel.comics.observe(viewLifecycleOwner) {comics ->
            comicsNetworkAdapter.updateData(comics)
        }
        binding.btnAddNewComicsNetwork.setOnClickListener {
            showAddComicsDialog()
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), AuthContainerActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        }
        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if(success) Toast.makeText(requireContext(), "Успешно удалено!", Toast.LENGTH_LONG).show(); viewModel.resetDeleteSuccess()
        }
        viewModel.postSuccess.observe(viewLifecycleOwner) { success ->
            if(success) Toast.makeText(requireContext(), "Успешно добавлено!", Toast.LENGTH_LONG).show(); viewModel.resetPostSuccess()
        }
        viewModel.downloadSuccess.observe(viewLifecycleOwner) { success ->
            if(success) Toast.makeText(requireContext(), "Успешно загруженно!", Toast.LENGTH_LONG).show(); viewModel.resetDownloadSuccess()
        }
        viewModel.refreshTrigger.observe(viewLifecycleOwner) { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.fetchComics()
                viewModel.resetRefreshTrigger()
            }
        }
        viewModel.fetchComics()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchComics()
    }

    override fun onItemClick(id: String) {
        val intent = Intent(requireContext(), ComicNetworkActivity::class.java)
        intent.putExtra("COMICS_ID", id)
        startActivity(intent)
    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteComics(id)
    }

    override fun onEditClick(id: String) {
        startActivity(Intent(requireContext(), EditNetworkActivity::class.java).putExtra("COMICS-ID", id))
    }

    override fun onSendClick(id: String) {
        TODO("Not yet implemented")
    }

    override fun onDownloadClick(id: String) {
        viewModel.downloadComic(id)
    }

    private fun showAddComicsDialog() {
        val inputName = EditText(requireContext()).apply {
            hint = "Введите название комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }

        val inputDesc = EditText(requireContext()).apply {
            hint = "Введите описание комикса"
            setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
        }
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputName)
            addView(inputDesc)
        }
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
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