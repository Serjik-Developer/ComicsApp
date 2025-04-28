package com.example.texnostrelka_2025_otbor.presentation.ui.userinfo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.databinding.ActivityInfoUserBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemComicsListener
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoActivity : AppCompatActivity(), OnItemComicsListener {
    private lateinit var adapter: ComicsNetworkAdapter
    private lateinit var binding: ActivityInfoUserBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = intent.getStringExtra("USER-ID") ?: throw IllegalArgumentException("USER-ID Is required")
        adapter = ComicsNetworkAdapter(mutableListOf(), this)
        viewModel.userData.observe(this) { userData ->
            adapter.updateData(userData.comics)
        }
        viewModel.error.observe(this) { error ->
            error?.let { showErrorDialog(it) }
        }
        viewModel.isSubscribed.observe(this) { isSubscribed ->

        }
        viewModel.fecthUserdata(userId)
    }

    override fun onItemClick(id: String) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }
}