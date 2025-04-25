package com.example.texnostrelka_2025_otbor.presentation.ui.infocomic

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.databinding.ActivityInfoComicBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.adapter.CommentsAdapter
import com.example.texnostrelka_2025_otbor.presentation.adapter.PageNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemCommentClickListener
import com.example.texnostrelka_2025_otbor.presentation.ui.auth.AuthActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.comicnetwork.ComicNetworkActivity
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
        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("COMICS_ID is required")
        adapterPage = PageNetworkAdapter(mutableListOf(), this)
        adapterComments = CommentsAdapter(mutableListOf(), this)
        binding.comicsFirstPageRecycler.layoutManager = LinearLayoutManager(this)
        binding.comicsFirstPageRecycler.adapter = adapterPage
        binding.commentsRecycler.layoutManager = LinearLayoutManager(this)
        binding.commentsRecycler.adapter = adapterComments
        binding.comicsFirstPageRecycler.setOnClickListener {
            startActivity(Intent(this, ComicNetworkActivity::class.java).putExtra("COMICS_ID", comicsId))
        }
        viewModel.comics.observe(this) { comics ->
            adapterPage.updateData(mutableListOf(comics.firstPage))
            adapterComments.updateComments(comics.comments)
            binding.authorName.text = comics.creator_name
            binding.authorName.setOnClickListener {
                startActivity(Intent(this, UserInfoActivity::class.java).putExtra("USER-ID", comics.creator))
            }
            binding.likesCount.text = comics.likesCount.toString()
            if (comics.userLiked) {
                binding.likeButton.setImageResource(R.drawable.ic_like_red)
            } else {
                binding.likeButton.setImageResource(R.drawable.ic_like)
            }
            if (comics.userFavorited) {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_yellow)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
            }
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
        binding.favoriteButton.setOnClickListener {

        }
        binding.likeButton.setOnClickListener {

        }
    }

    override fun onDeleteClick(commentId: String) {
        viewModel.deleteComment(commentId)
    }

    override fun onUserClick(userId: String) {
        TODO("Not yet implemented")
    }
}