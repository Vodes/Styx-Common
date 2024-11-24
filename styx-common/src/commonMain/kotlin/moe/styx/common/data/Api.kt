package moe.styx.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Changes(val media: Long, val entry: Long)

@Serializable
data class APIState(val lastUpdatedTraffic: Long)

@Serializable
data class LoginResponse(
    val name: String, val userID: String, val permissions: Int, val accessToken: String, val watchToken: String,
    val tokenExpiry: Long, val refreshToken: String? = null
)

@Serializable
data class CreationResponse(val GUID: String, val code: Int, val expiry: Long)

/**
 * Generic api response used to display errors on the user side.
 *
 * @param code HTTP Status code.
 * @param message The message shown to the user
 * @param silent If it should be brought up to the user at all or not.
 */
@Serializable
data class ApiResponse(var code: Int, var message: String?, var silent: Boolean = false)


@Serializable
data class Log(val userID: String, val deviceID: String, val type: LogType, val content: String?, val time: Long)

enum class LogType {
    LOGIN, LOGOUT, WATCH, OTHER
}

/**
 *  Data type representing the media that a user might currently be playing.
 *
 * @param mediaEntry ID of the current entry being played.
 * @param time Where the user is currently at in playback
 * @param playing If the user is currently playing or paused
 */
@Serializable
data class MediaActivity(val mediaEntry: String, val time: Long, val playing: Boolean)

/**
 * Data type that the client will be sending every few seconds.
 *
 * @param token Current login token representing the user and device
 * @param mediaActivity Current media activity if anything is playing/opened. Otherwise null.
 * @param listeningTo If the user is "listening" to another user's playback, this will have an ID. (For group-watch)
 */
@Serializable
data class ClientHeartbeat(val token: String, val mediaActivity: MediaActivity? = null, val listeningTo: String? = null)

/**
 * Data type used for the online user list in clients.
 *
 * @param user User class carrying various information that might be useful
 * @param deviceID Device ID. The clients don't really need this, but it's useful in the database.
 * @param deviceType Usually some form of "PC" or "Phone"
 * @param lastPing Unix Timestamp for the last received ping
 * @param mediaActivity Current Media being played. Can be null.
 * @param listeningTo If the user is "listening" to another user's playback, this will have an ID. (For group-watch)
 */
@Serializable
data class ActiveUser(
    val user: User,
    val deviceID: String,
    val deviceType: String,
    val lastPing: Long? = null,
    val mediaActivity: MediaActivity? = null,
    val listeningTo: String? = null
)