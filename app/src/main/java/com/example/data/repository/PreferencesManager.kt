package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ConnectionConfig
import com.example.data.model.UserLicense
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("maxo_dashboard_prefs", Context.MODE_PRIVATE)

    companion object {
        // Shizuku keys
        private const val KEY_SHIZUKU_TOKEN = "MAXO_GITHUB_TOKEN"
        private const val KEY_SHIZUKU_RAW = "maxo_shizuku_raw"

        // Link keys
        private const val KEY_LINK_TOKEN = "maxo_link_token"
        private const val KEY_LINK_RAW = "maxo_link_raw"

        // Users keys
        private const val KEY_USERS_TOKEN = "savix_token"
        private const val KEY_USERS_RAW = "savix_raw_url"

        // Disabled users key
        private const val KEY_DISABLED_USERS = "disabled_users"
    }

    // --- Shizuku Config ---
    fun getShizukuConfig(): ConnectionConfig {
        val token = prefs.getString(KEY_SHIZUKU_TOKEN, "") ?: ""
        val raw = prefs.getString(KEY_SHIZUKU_RAW, "") ?: ""
        return ConnectionConfig.from(token, raw)
    }

    fun saveShizukuConfig(token: String, rawUrl: String) {
        prefs.edit()
            .putString(KEY_SHIZUKU_TOKEN, token.trim())
            .putString(KEY_SHIZUKU_RAW, rawUrl.trim())
            .apply()
    }

    fun clearShizukuConfig() {
        prefs.edit().remove(KEY_SHIZUKU_TOKEN).remove(KEY_SHIZUKU_RAW).apply()
    }

    // --- Link Config ---
    fun getLinkConfig(): ConnectionConfig {
        var token = prefs.getString(KEY_LINK_TOKEN, "") ?: ""
        // Legacy fallback from HTML
        if (token.isBlank()) {
            token = prefs.getString(KEY_SHIZUKU_TOKEN, "") ?: ""
        }
        val raw = prefs.getString(KEY_LINK_RAW, "") ?: ""
        return ConnectionConfig.from(token, raw)
    }

    fun saveLinkConfig(token: String, rawUrl: String) {
        prefs.edit()
            .putString(KEY_LINK_TOKEN, token.trim())
            .putString(KEY_LINK_RAW, rawUrl.trim())
            .apply()
    }

    fun clearLinkConfig() {
        prefs.edit().remove(KEY_LINK_TOKEN).remove(KEY_LINK_RAW).apply()
    }

    // --- Users Config ---
    fun getUsersConfig(): ConnectionConfig {
        val token = prefs.getString(KEY_USERS_TOKEN, "") ?: ""
        val raw = prefs.getString(KEY_USERS_RAW, "") ?: ""
        return ConnectionConfig.from(token, raw)
    }

    fun saveUsersConfig(token: String, rawUrl: String) {
        prefs.edit()
            .putString(KEY_USERS_TOKEN, token.trim())
            .putString(KEY_USERS_RAW, rawUrl.trim())
            .apply()
    }

    fun clearUsersConfig() {
        prefs.edit().remove(KEY_USERS_TOKEN).remove(KEY_USERS_RAW).apply()
    }

    // --- Disabled Users List ---
    fun getDisabledUsers(): List<UserLicense> {
        val json = prefs.getString(KEY_DISABLED_USERS, null) ?: return emptyList()
        val list = mutableListOf<UserLicense>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    UserLicense(
                        deviceId = obj.optString("device_id", ""),
                        key = obj.optString("key", ""),
                        expiryDate = obj.optString("expirydate", ""),
                        allowOffline = obj.optBoolean("Allowoffline", true),
                        isDisabled = true
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveDisabledUsers(list: List<UserLicense>) {
        val arr = JSONArray()
        list.forEach { u ->
            val obj = JSONObject()
            obj.put("device_id", u.deviceId)
            obj.put("key", u.key)
            obj.put("expirydate", u.expiryDate)
            obj.put("Allowoffline", u.allowOffline)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_DISABLED_USERS, arr.toString()).apply()
    }
}
