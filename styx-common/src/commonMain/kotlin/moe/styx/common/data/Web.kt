package moe.styx.common.data

import kotlinx.serialization.Serializable

@Serializable
data class WebTempLink(
    val urlSegment: String,
    val createdAt: Long,
    val expiresAt: Long,
    val userID: String,
    val file: String,
)

@Serializable
data class WebLogin(
    val userID: String,
    val createdAt: Long,
    val expiresAt: Long,
    val token: String
)

@Serializable
data class ShowVoting(
    val title: String,
    val anilistID: Int,
    val votes: Int,
    val hasVeto: Boolean,
    val serverID: Long,
    val channelID: Long,
    val messageID: Long
)