package com.example.data.model

data class RepoInfo(
    val owner: String,
    val repo: String,
    val branch: String,
    val path: String
)

data class ConnectionConfig(
    val token: String = "",
    val rawUrl: String = "",
    val parsedInfo: RepoInfo? = null,
    val parseError: String? = null
) {
    val isSaved: Boolean
        get() = token.isNotBlank() && rawUrl.isNotBlank() && parsedInfo != null

    companion object {
        fun parseRawUrl(rawInput: String): RepoInfo {
            val raw = rawInput.trim()
            val prefix = "https://raw.githubusercontent.com/"
            if (!raw.startsWith(prefix)) {
                throw IllegalArgumentException("RAW URL must start with https://raw.githubusercontent.com/")
            }
            val pieces = raw.substring(prefix.length).split("/")
            if (pieces.size < 4) {
                throw IllegalArgumentException("Invalid Raw GitHub URL")
            }
            val owner = pieces[0]
            val repo = pieces[1]
            val branch: String
            val filePath: String

            if (pieces.size >= 5 && pieces[2] == "refs" && pieces[3] == "heads") {
                branch = pieces[4]
                filePath = pieces.drop(5).joinToString("/")
            } else {
                branch = pieces[2]
                filePath = pieces.drop(3).joinToString("/")
            }

            if (owner.isBlank() || repo.isBlank() || branch.isBlank() || filePath.isBlank()) {
                throw IllegalArgumentException("Could not read repository information")
            }
            return RepoInfo(owner = owner, repo = repo, branch = branch, path = filePath)
        }

        fun from(token: String, rawUrl: String): ConnectionConfig {
            return if (rawUrl.isBlank()) {
                ConnectionConfig(token = token, rawUrl = rawUrl, parsedInfo = null, parseError = null)
            } else {
                try {
                    val info = parseRawUrl(rawUrl)
                    ConnectionConfig(token = token, rawUrl = rawUrl, parsedInfo = info, parseError = null)
                } catch (e: Exception) {
                    ConnectionConfig(token = token, rawUrl = rawUrl, parsedInfo = null, parseError = e.message)
                }
            }
        }
    }
}
