package com.example.texnostrelka_2025_otbor.presentation.ui.userinfo

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.texnostrelka_2025_otbor.R
import com.example.texnostrelka_2025_otbor.data.remote.model.subscribe.SubscriptionMode
import com.example.texnostrelka_2025_otbor.databinding.ActivityInfoUserBinding
import com.example.texnostrelka_2025_otbor.presentation.adapter.ComicsNetworkAdapter
import com.example.texnostrelka_2025_otbor.presentation.listener.OnItemComicsListener
import com.example.texnostrelka_2025_otbor.presentation.ui.comicnetwork.ComicNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.userinfo.fragments.SubscribedUsersFragment
import com.example.texnostrelka_2025_otbor.presentation.utils.DialogHelper.showErrorDialog
import com.example.texnostrelka_2025_otbor.presentation.utils.base64ToBitmap
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
        userId = intent.getStringExtra("USER-ID") ?: throw IllegalArgumentException("USER-ID is required")
        adapter = ComicsNetworkAdapter(mutableListOf(), this)
        binding.recyclerViewComics.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewComics.adapter = adapter
        viewModel.userData.observe(this) { userData ->
            adapter.updateData(userData.comics)
            if (userData.is_subscribed) binding.btnSubscribe.text = "Отписаться"
            else binding.btnSubscribe.text = "Подписаться"
            binding.textViewUsername.text = userData.name
            binding.textViewSubscribersCount.text = userData.subscribers_count.toString()
            binding.textViewFollowingCount.text = userData.subscriptions_count.toString()
            userData.avatar?.let {
                binding.imageViewAvatar.setImageBitmap(it.base64ToBitmap())
            } ?: run {
                binding.imageViewAvatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }
        }
        viewModel.error.observe(this) { error ->
            error?.let { showErrorDialog(it) }
        }
        viewModel.isSubscribed.observe(this) { isSubscribed ->
            if (isSubscribed) binding.btnSubscribe.text = "Отписаться"
            else binding.btnSubscribe.text = "Подписаться"
        }
        binding.FollowingCountLinearLayout.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, SubscribedUsersFragment.newInstance(SubscriptionMode.FOLLOWING))
                .addToBackStack(null)
                .commit()
        }
        binding.SubscribersCountLinearLayout.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, SubscribedUsersFragment.newInstance(SubscriptionMode.SUBSCRIBERS))
                .addToBackStack(null)
                .commit()
        }
        binding.btnSubscribe.setOnClickListener {
            viewModel.postSubscribe(userId)
        }
        viewModel.fecthUserdata(userId)
    }

    override fun onItemClick(id: String) {
        startActivity(Intent(this, ComicNetworkActivity::class.java).putExtra("COMICS_ID", id))
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