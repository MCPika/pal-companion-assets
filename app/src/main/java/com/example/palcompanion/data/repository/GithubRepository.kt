package com.example.palcompanion.data.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

@Serializable
data class CommitInfo(val sha: String)

interface GithubRepository {
    suspend fun getLatestCommitHash(owner: String, repo: String, branch: String): String?
}

class DefaultGithubRepository : GithubRepository {
    override suspend fun getLatestCommitHash(owner: String, repo: String, branch: String): String? {
        return try {
            val url = "https://api.github.com/repos/$owner/$repo/commits/$branch"
            val jsonString = URL(url).readText()
            val json = Json { ignoreUnknownKeys = true }
            val commitInfo = json.decodeFromString<CommitInfo>(jsonString)
            commitInfo.sha
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
