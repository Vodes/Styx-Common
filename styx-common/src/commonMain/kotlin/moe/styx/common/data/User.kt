package moe.styx.common.data

import kotlinx.serialization.Serializable
import moe.styx.common.extension.currentUnixSeconds

@Serializable
data class User(
    val GUID: String, var name: String, var discordID: String, val added: Long, var lastLogin: Long,
    var permissions: Int, var anilistData: AnilistData? = null
)

@Serializable
data class AnilistData(val accessToken: String, val refreshToken: String, val tokenExpiry: Long, val userName: String, val userID: Int)

@Serializable
data class DeviceInfo(
    val type: String, val name: String?, val model: String?, val cpu: String?, val gpu: String?,
    val os: String, val osVersion: String?, var jvm: String?, var jvmVersion: String?, val appSecret: String
)

@Serializable
data class UnregisteredDevice(val GUID: String, val deviceInfo: DeviceInfo, val codeExpiry: Long, val code: Int)

@Serializable
data class Device(
    var GUID: String, var userID: String, var name: String, var deviceInfo: DeviceInfo,
    var lastUsed: Long, var accessToken: String, var watchToken: String, var refreshToken: String, var tokenExpiry: Long,
    var added: Long, var isDevDevice: Int
)

fun UnregisteredDevice.toDevice(userID: String, name: String): Device {
    return Device(GUID, userID, name, deviceInfo, -1, "", "", "", -1, currentUnixSeconds(), 0)
}

@Serializable
data class Favourite(val mediaID: String, var userID: String, var added: Long)

@Serializable
data class QueuedFavChanges(var toAdd: MutableList<Favourite> = mutableListOf(), val toRemove: MutableList<Favourite> = mutableListOf())

@Serializable
data class QueuedWatchedChanges(var toUpdate: MutableList<MediaWatched> = mutableListOf(), val toRemove: MutableList<MediaWatched> = mutableListOf())