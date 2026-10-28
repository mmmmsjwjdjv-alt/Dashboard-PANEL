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
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class GitHubRepository(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
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

    suspend fun getFile(token: String, info: RepoInfo): Pair<GitHubFileResponse, String> =
        withContext(Dispatchers.IO) {
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
                    val errorMsg = if (response.code == 401) {
                        "Unauthorized (401). Invalid token or token lacks Contents read permissions."
                    } else if (response.code == 404) {
                        "File not found (404). Check owner, repo, branch, or file path."
                    } else {
                        "GitHub READ ${response.code}: $bodyStr"
                    }
                    throw Exception(errorMsg)
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
        }

    suspend fun updateFile(token: String, info: RepoInfo, content: String): String =
        withContext(Dispatchers.IO) {
            // First fetch the latest SHA
            val (currentFile, _) = getFile(token, info)
            val currentSha = currentFile.sha

            val url = buildApiUrl(info, includeRef = false)
            val encodedContent = encodeContent(content)

            val payload = JSONObject().apply {
                put("message", "MAXO update ${info.path}")
                put("content", encodedContent)
                put("sha", currentSha)
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
                    throw Exception("GitHub UPDATE ${response.code}: $bodyStr")
                }
                val resJson = JSONObject(bodyStr)
                val contentObj = resJson.optJSONObject("content")
                contentObj?.optString("sha") ?: resJson.optString("sha", "")
            }
        }

    suspend fun fetchUsers(token: String, info: RepoInfo): Pair<String, List<UserLicense>> =
        withContext(Dispatchers.IO) {
            val (fileResp, decodedJson) = getFile(token, info)
            val list = mutableListOf<UserLicense>()
            if (decodedJson.isNotBlank()) {
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
            }
            Pair(fileResp.sha, list)
        }

    suspend fun pushUsers(
        token: String,
        info: RepoInfo,
        shaToUse: String,
        usersList: List<UserLicense>,
        commitMessage: String
    ): String = withContext(Dispatchers.IO) {
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
            put("sha", shaToUse)
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
                throw Exception("GitHub sync failed (${response.code}): $bodyStr")
            }
            val resJson = JSONObject(bodyStr)
            val contentObj = resJson.optJSONObject("content")
            contentObj?.optString("sha") ?: resJson.optString("sha", "")
        }
    }
}
