package com.example.texnostrelka_2025_otbor.presentation.ui.infocomic

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.databinding.ActivityInfoComicBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.CommentsAdapter
import com.example.texnostrelka_2025_otbor.presentation.adapter.PageNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemCommentClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoComicActivity : AppCompatActivity(), OnItemCommentClickListener {
    private lateinit var binding: ActivityInfoComicBinding
    private val viewModel: InfoComicViewModel by viewModels()
    private var comicsId = ""
    private lateinit var adapterPage: PageNetworkAdapter
    private lateinit var adapterComments: CommentsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoComicBinding.inflate(layoutInflater)
        adapterPage = PageNetworkAdapter(mutableListOf(), this)
        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("COMICS_ID is required")
        viewModel.comics.observe(this) { comics ->
            adapterPage.updateData(mutableListOf(comics.firstPage))
            adapterComments.updateComments(comics.comments)
        }
        viewModel.error.observe(this) { error ->
            error?.let {
                if (it == "Не авторизован.") {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, AuthActivity::class.java))
                } else {
                    showErrorDialog(it)
                }
            }
        }
        viewModel.success.observe(this) { success ->
            success?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.fetchInfo(comicsId)
    }

    override fun onDeleteClick(commentId: String) {
        viewModel.deleteComment(commentId)
    }

    override fun onUserClick(userId: String) {
        TODO("Not yet implemented")
    }
}