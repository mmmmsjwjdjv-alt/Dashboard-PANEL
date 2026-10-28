package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SessionLog(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val timestamp: Long = System.currentTimeMillis(),
    val text: String
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
}

data class GitHubFileResponse(
    val sha: String = "",
    val content: String = "",
    val encoding: String = "",
    val size: Long = 0L,
    val name: String = "",
    val path: String = ""
)

data class GitHubUpdateRequest(
    val message: String,
    val content: String,
    val sha: String,
    val branch: String
)
