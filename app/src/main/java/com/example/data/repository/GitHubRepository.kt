package com.example.data.repository

import android.util.Base64
import com.example.data.model.GitHubFileResponse
import com.example.data.model.RepoInfo
import com.example.data.model.UserLicense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class GitHubRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
) {

    private fun urlEncode(value: String): String = URLEncoder.encode(value, "UTF-8")

    private fun buildApiUrl(info: RepoInfo, includeRef: Boolean = true): String {
        val pathParts = info.path.split("/").map { urlEncode(it) }.joinToString("/")
        val baseUrl = "https://api.github.com/repos/${urlEncode(info.owner)}/${urlEncode(info.repo)}/contents/$pathParts"
        return if (includeRef) "$baseUrl?ref=${urlEncode(info.branch)}" else baseUrl
    }

    private fun decodeContent(rawContent: String): String {
        val clean = rawContent.replace("\\s".toRegex(), "")
        val bytes = Base64.decode(clean, Base64.DEFAULT)
        return String(bytes, Charsets.UTF_8)
    }

    private fun encodeContent(text: String): String {
        val bytes = text.toByteArray(Charsets.UTF_8)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun parseHttpError(code: Int, body: String): String {
        return when (code) {
            401 -> "توکن گیت‌هاب نامعتبر یا منقضی شده است (401 Unauthorized). لطفاً توکن معتبر با دسترسی Contents وارد کنید."
            403 -> "دسترسی مسدود است یا سقف درخواست‌های گیت‌هاب پر شده است (403 Forbidden). مطمئن شوید توکن دسترسی Read & Write دارد."
            404 -> "مخزن یا فایل در گیت‌هاب یافت نشد (404 Not Found). نام کاربری، مخزن، برنچ یا مسیر فایل را بررسی کنید."
            409 -> "تداخل در نسخه فایل گیت‌هاب (409 Conflict). فایل در حال حاضر توسط نسخه جدیدتر تغییر کرده است."
            422 -> "خطای ساختار داده در گیت‌هاب (422 Unprocessable). برنچ یا مسیر معتبر نیست."
            else -> "خطای گیت‌هاب ($code): ${if (body.length > 120) body.take(120) + "..." else body}"
        }
    }

    suspend fun getFile(token: String, info: RepoInfo): Pair<GitHubFileResponse, String> =
        withContext(Dispatchers.IO) {
            try {
                val url = buildApiUrl(info, includeRef = true)
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/vnd.github+json")
                    .addHeader("Authorization", "Bearer ${token.trim()}")
                    .addHeader("X-GitHub-Api-Version", "2022-11-28")
                    .addHeader("User-Agent", "MAXO-Dashboard-Android")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val bodyStr = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        throw Exception(parseHttpError(response.code, bodyStr))
                    }

                    val json = JSONObject(bodyStr)
                    val sha = json.optString("sha", "")
                    val contentEncoded = json.optString("content", "")
                    val size = json.optLong("size", 0L)
                    val name = json.optString("name", "")
                    val path = json.optString("path", "")

                    val decoded = decodeContent(contentEncoded)
                    val fileResponse = GitHubFileResponse(
                        sha = sha,
                        content = contentEncoded,
                        encoding = json.optString("encoding", "base64"),
                        size = size,
                        name = name,
                        path = path
                    )
                    Pair(fileResponse, decoded)
                }
            } catch (e: UnknownHostException) {
                throw Exception("عدم دسترسی به اینترنت یا سرور گیت‌هاب. وضعیت اینترنت یا فیلترشکن را بررسی کنید.")
            } catch (e: SocketTimeoutException) {
                throw Exception("مهلت اتصال به گیت‌هاب به پایان رسید (Timeout). سرعت اینترنت را بررسی کنید.")
            } catch (e: IOException) {
                throw Exception(e.message ?: "خطای ارتباط شبکه رخ داد.")
            }
        }

    suspend fun updateFile(token: String, info: RepoInfo, content: String): String =
        withContext(Dispatchers.IO) {
            try {
                // Check if file already exists to get SHA; if 404, we create it freshly
                var existingSha: String? = null
                try {
                    val (currentFile, _) = getFile(token, info)
                    existingSha = currentFile.sha.ifBlank { null }
                } catch (e: Exception) {
                    if (e.message?.contains("404") != true) {
                        // ignore 404 so we can create newly
                    }
                }

                val url = buildApiUrl(info, includeRef = false)
                val encodedContent = encodeContent(content)

                val payload = JSONObject().apply {
                    put("message", "MAXO update ${info.path}")
                    put("content", encodedContent)
                    if (existingSha != null) {
                        put("sha", existingSha)
                    }
                    put("branch", info.branch)
                }

                val requestBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/vnd.github+json")
                    .addHeader("Authorization", "Bearer ${token.trim()}")
                    .addHeader("X-GitHub-Api-Version", "2022-11-28")
                    .addHeader("User-Agent", "MAXO-Dashboard-Android")
                    .put(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    val bodyStr = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        throw Exception(parseHttpError(response.code, bodyStr))
                    }
                    val resJson = JSONObject(bodyStr)
                    val contentObj = resJson.optJSONObject("content")
                    contentObj?.optString("sha") ?: resJson.optString("sha", "")
                }
            } catch (e: UnknownHostException) {
                throw Exception("عدم دسترسی به اینترنت یا سرور گیت‌هاب. اینترنت یا VPN را بررسی نمایید.")
            } catch (e: SocketTimeoutException) {
                throw Exception("تایم‌اوت اتصال به گیت‌هاب.")
            }
        }

    suspend fun fetchUsers(token: String, info: RepoInfo): Pair<String, List<UserLicense>> =
        withContext(Dispatchers.IO) {
            try {
                val (fileResp, decodedJson) = getFile(token, info)
                val list = mutableListOf<UserLicense>()
                if (decodedJson.isNotBlank()) {
                    try {
                        val jsonArray = JSONArray(decodedJson.trim())
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            list.add(
                                UserLicense(
                                    deviceId = obj.optString("device_id", ""),
                                    key = obj.optString("key", ""),
                                    expiryDate = obj.optString("expirydate", ""),
                                    allowOffline = obj.optBoolean("Allowoffline", true),
                                    isDisabled = false
                                )
                            )
                        }
                    } catch (jsonEx: Exception) {
                        // In case the file has unexpected structure
                    }
                }
                Pair(fileResp.sha, list)
            } catch (e: Exception) {
                if (e.message?.contains("404") == true) {
                    // File does not exist yet on GitHub, return empty list cleanly
                    Pair("", emptyList())
                } else {
                    throw e
                }
            }
        }

    suspend fun pushUsers(
        token: String,
        info: RepoInfo,
        shaToUse: String,
        usersList: List<UserLicense>,
        commitMessage: String
    ): String = withContext(Dispatchers.IO) {
        try {
            var activeSha = shaToUse
            // If activeSha is blank, try to fetch current SHA once
            if (activeSha.isBlank()) {
                try {
                    val (currentFile, _) = getFile(token, info)
                    activeSha = currentFile.sha
                } catch (_: Exception) {}
            }

            val jsonArray = JSONArray()
            usersList.forEach { u ->
                val obj = JSONObject()
                obj.put("device_id", u.deviceId)
                obj.put("key", u.key)
                obj.put("expirydate", u.expiryDate)
                obj.put("Allowoffline", u.allowOffline)
                jsonArray.put(obj)
            }
            val prettyJson = jsonArray.toString(4)
            val encodedContent = encodeContent(prettyJson)

            val url = buildApiUrl(info, includeRef = false)
            val payload = JSONObject().apply {
                put("message", commitMessage)
                put("content", encodedContent)
                if (activeSha.isNotBlank()) {
                    put("sha", activeSha)
                }
                put("branch", info.branch)
            }

            val requestBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader("Authorization", "Bearer ${token.trim()}")
                .addHeader("X-GitHub-Api-Version", "2022-11-28")
                .addHeader("User-Agent", "MAXO-Dashboard-Android")
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string().orEmpty()
                if (response.code == 409) {
                    // Handle SHA conflict: re-fetch latest SHA and retry once
                    val (freshFile, _) = getFile(token, info)
                    payload.put("sha", freshFile.sha)
                    val retryBody = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
                    val retryRequest = request.newBuilder().put(retryBody).build()
                    client.newCall(retryRequest).execute().use { retryResp ->
                        val retryBodyStr = retryResp.body?.string().orEmpty()
                        if (!retryResp.isSuccessful) {
                            throw Exception(parseHttpError(retryResp.code, retryBodyStr))
                        }
                        val retryJson = JSONObject(retryBodyStr)
                        val contentObj = retryJson.optJSONObject("content")
                        return@withContext contentObj?.optString("sha") ?: retryJson.optString("sha", "")
                    }
                }

                if (!response.isSuccessful) {
                    throw Exception(parseHttpError(response.code, bodyStr))
                }
                val resJson = JSONObject(bodyStr)
                val contentObj = resJson.optJSONObject("content")
                contentObj?.optString("sha") ?: resJson.optString("sha", "")
            }
        } catch (e: UnknownHostException) {
            throw Exception("عدم دسترسی به اینترنت یا سرور گیت‌هاب.")
        } catch (e: SocketTimeoutException) {
            throw Exception("تایم‌اوت اتصال به گیت‌هاب.")
        }
    }
}
