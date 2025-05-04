package com.example.texnostrelka_2025_otbor.data.remote.model.user

data class CurrentUserInfoResponseModel(
    val id: String,
    val login: String,
    val name: String,
    val avatar: String? = null,
    val isEnebledNotifications: Boolean
)