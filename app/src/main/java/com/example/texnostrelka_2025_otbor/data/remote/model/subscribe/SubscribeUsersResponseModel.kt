package com.example.texnostrelka_2025_otbor.data.remote.model.subscribe

data class SubscribeUsersResponseModel(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val is_subscribed_by_me: Boolean? = null
)