package com.example.texnostrelka_2025_otbor.database

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefernces", Context.MODE_PRIVATE)
    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }
    fun getAuthToken() : String? {
        return sharedPreferences.getString("auth_token", null)
    }
}