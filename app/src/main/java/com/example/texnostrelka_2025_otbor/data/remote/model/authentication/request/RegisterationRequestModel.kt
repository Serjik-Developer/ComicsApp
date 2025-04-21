package com.example.texnostrelka_2025_otbor.data.remote.model.authentication.request

data class RegisterationRequestModel(
    val login: String,
    val password: String,
    val name: String
)