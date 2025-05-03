package com.example.texnostrelka_2025_otbor.data.local.preferences

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
    fun saveName(name:String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }
    fun getName() : String? {
        return sharedPreferences.getString("user_name", null)
    }
    fun clearAuthToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }
    fun clearName() {
        sharedPreferences.edit().remove("user_name").apply()
    }
    fun savePendingFcmToken(token: String) {
        sharedPreferences.edit().putString("fcm_token", token).apply()
    }
}