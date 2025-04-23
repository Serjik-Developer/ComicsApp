package com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.data.local.database.ComicsDatabase
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.FragmentViewNetworkBinding
import com.example.texnostrelka_2025_otbor.domain.repository.ComicsRepository
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemClickListener
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemComicsListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.comicnetwork.ComicNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.main.fragments.viewmodels.ViewNetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewNetworkFragment : Fragment(), OnItemComicsListener {
    private var _binding : FragmentViewNetworkBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ViewNetworkViewModel by activityViewModels ()
    private lateinit var comicsNetworkAdapter: ComicsNetworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewNetworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comicsNetworkAdapter = ComicsNetworkAdapter(mutableListOf(), this)
        binding.RecyclerViewNetwork.layoutManager = LinearLayoutManager(requireContext())
        binding.RecyclerViewNetwork.adapter = comicsNetworkAdapter
        viewModel.comics.observe( viewLifecycleOwner) { comics ->
            comicsNetworkAdapter.updateData(comics)
        }
        viewModel.downloadSuccess.observe(viewLifecycleOwner) { success ->
            if(success) Toast.makeText(requireContext(), "Успешно загруженно!", Toast.LENGTH_LONG).show()
            viewModel.resetDownloadSuccess()
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), AuthActivity::class.java))
                }
                else {
                    showErrorDialog(error)
                }
            }
        }
        viewModel.fetchComics()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
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
        val intent = Intent(requireContext(), ComicNetworkActivity::class.java)
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

    override fun onDownloadClick(id: String) {
        viewModel.downloadComic(id)
    }
}