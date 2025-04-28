package com.example.texnostrelka_2025_otbor.presentation.ui.infocomic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
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
import com.example.texnostrelka_2025_otbor.presentation.ui.userinfo.UserInfoActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        setContentView(binding.root)
        comicsId = intent.getStringExtra("COMICS_ID") ?: throw IllegalArgumentException("COMICS_ID is required")
        adapterPage = PageNetworkAdapter(mutableListOf(), this)
        adapterComments = CommentsAdapter(mutableListOf(), this)
        binding.comicsFirstPageRecycler.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.comicsFirstPageRecycler.adapter = adapterPage
        binding.commentsRecycler.layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean = false
        }
        binding.commentsRecycler.adapter = adapterComments
        viewModel.comics.observe(this) { comics ->
            Log.w("DATA-COMICS", comics.toString())
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
        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) binding.favoriteButton.setImageResource(R.drawable.ic_favorite_yellow)
            else binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
        }
        viewModel.isLiked.observe(this) { isLiked ->
            if(isLiked) {
                binding.likeButton.setImageResource(R.drawable.ic_like_red)
                binding.likesCount.text = "${binding.likesCount.text.toString().toInt() + 1}"
            }
            else {
                binding.likeButton.setImageResource(R.drawable.ic_like)
                binding.likesCount.text = "${binding.likesCount.text.toString().toInt() - 1}"
            }
        }
        viewModel.refreshTrigger.observe(this) { shouldRefresh ->
            if(shouldRefresh) {
                viewModel.fetchInfo(comicsId)
                viewModel.resetRefreshTrigger()
            }
        }
        viewModel.fetchInfo(comicsId)
        binding.favoriteButton.setOnClickListener {
            viewModel.postFavorite(comicsId)
        }
        binding.likeButton.setOnClickListener {
            viewModel.postLike(comicsId)
        }
        binding.addCommentButton.setOnClickListener {
            addCommentDialog()
        }
        binding.viewButton.setOnClickListener {
            startActivity(Intent(this, ComicNetworkActivity::class.java).putExtra("COMICS_ID", comicsId))
        }
        binding.downloadButton.setOnClickListener {
            viewModel.downloadComic(comicsId)
        }
    }

    private fun addCommentDialog() {
        val inputText = EditText(this).apply {
            hint = "Введите текст комментария"
        }

        MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setTitle("Добавить комментарий")
            .setView(LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(50, 20, 50, 20)
                addView(inputText)
            })
            .setPositiveButton("Добавить") { _, _ ->
                val text = inputText.text.toString()
                viewModel.postComment(text, comicsId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDeleteClick(commentId: String) {
        viewModel.deleteComment(commentId)
    }

    override fun onUserClick(userId: String) {
        startActivity(Intent(this, UserInfoActivity::class.java).putExtra("USER-ID", userId))
    }
}