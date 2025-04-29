package com.example.texnostrelka_2025_otbor.data.remote.model.user

import com.example.texnostrelka_2025_otbor.data.remote.model.comic.ComicsCoverNetworkModel

data class InfoUserResponseModel(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val total_likes: Int,
    val subscribers_count: Int,
    val subscriptions_count: Int,
    val is_subscribed: Boolean? = null,
    val comics: MutableList<ComicsCoverNetworkModel>
)
