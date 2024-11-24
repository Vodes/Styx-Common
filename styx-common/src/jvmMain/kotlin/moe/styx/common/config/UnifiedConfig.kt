package moe.styx.common.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import moe.styx.common.extension.currentUnixSeconds
import moe.styx.common.isDocker
import moe.styx.common.isWindows
import moe.styx.common.toml
import moe.styx.common.util.Log
import moe.styx.common.util.getEnvBool
import moe.styx.common.util.getEnvString
import net.peanuuutz.tomlkt.TomlComment
import net.peanuuutz.tomlkt.decodeFromString
import java.io.File
import kotlin.system.exitProcess

@Serializable
data class UnifiedConfig(
    @SerialName("General")
    val base: BaseConfig = BaseConfig(),
    @SerialName("Database-Config")
    val dbConfig: DatabaseConfig = DatabaseConfig(),
    @SerialName("StyxWeb-Config")
    val webConfig: WebConfig = WebConfig(),
    @SerialName("StyxAPI-Config")
    val apiConfig: APIConfig = APIConfig(),
    @SerialName("Discord-Config")
    val discord: DiscordConfig = DiscordConfig(),
    @SerialName("Downloader-Config")
    val dlConfig: DownloaderConfig = DownloaderConfig()
) {
    companion object {
        private val configDir: File by lazy {
            if (isDocker)
                File("/config")
            else if (isWindows) {
                File(System.getenv("APPDATA"), "Styx-Server")
            } else {
                val xdgDirEnv = System.getenv("XDG_CONFIG_HOME")
                val userConfDir = if (!xdgDirEnv.isNullOrBlank() && File(xdgDirEnv).exists()) File(xdgDirEnv) else
                    File(System.getProperty("user.home"), ".config")
                File(userConfDir, "Styx-Server")
            }
        }

        val configFile by lazy {
            File(configDir, "config.toml")
        }

        private var lastUpdated: Long = 0L
        private var _current: UnifiedConfig? = null
        val current: UnifiedConfig
            get() {
                if (!configDir.exists()) {
                    runCatching { configDir.mkdirs() }
                        .onFailure {
                            Log.e(exception = it) { "Could not create config directory!\nPlease double check the permissions." }
                            exitProcess(1)
                        }
                }
                if (!configDir.canWrite()) {
                    Log.e { "Cannot write to config directory!\nPlease double check the permissions." }
                    exitProcess(1)
                }

                val now = currentUnixSeconds()
                if (!configFile.exists()) {
                    Log.w { "Config File has been created at: ${configFile.absolutePath}\nPlease do the necessary edits." }
                    return UnifiedConfig().also {
                        _current = it
                        configFile.writeText(toml.encodeToString(it))
                    }
                }
                val wasChanged = configFile.exists() && (configFile.lastModified() / 1000).toInt() > lastUpdated
                if (_current == null || lastUpdated < (now - 600) || wasChanged) {
                    _current = toml.decodeFromString(configFile.readText())
                    lastUpdated = now
                }
                return _current!!
            }

        fun updateConfig(update: (UnifiedConfig) -> UnifiedConfig) {
            val updated = update(current)
            _current = updated
            configFile.writeText(toml.encodeToString(updated))
            lastUpdated = currentUnixSeconds() + 1
        }
    }
}

@Serializable
data class BaseConfig(
    @TomlComment("Enable or disable debug logging. Will prefer 'DEBUG' env variable if any.")
    private val debug: Boolean = false,

    @TomlComment("Base URL for styx-web. Will prefer 'BASE_URL' env variable if any.")
    private val siteBaseURL: String = "https://example.com",

    @TomlComment("Base URL for styx-api. Will prefer 'API_BASE_URL' env variable if any.")
    private val apiBaseURL: String = "https://api.example.com",

    @TomlComment(
        """
        Base URL for the images directory. Will prefer 'IMAGE_URL' env variable if any.
        You can leave this empty and use the API for image access under 'apiBaseURL/image/file.ext' but it will be slower than a good webserver. 
    """
    )
    private val imageBaseURL: String = "https://images.example.com",

    @TomlComment("Directory containing all the images. Will prefer 'IMAGE_DIR' env variable if any.")
    private val imageDir: String = "/images",

    @TomlComment("Directory containing all the desktop app builds. Will prefer 'BUILD_DIR' env variable if any.")
    private val buildDir: String = "/builds",

    @TomlComment("Directory containing all the android app builds. Will prefer 'ANDROID_BUILD_DIR' env variable if any.")
    private val androidBuildDir: String = "/android-builds",

    @TomlComment("Directory containing all the mpv bundles. Will prefer 'MPV_DIR' env variable if any.")
    private val mpvDir: String = "/mpv",

    @TomlComment("User-Agent used for all outgoing http requests. Will prefer 'USER_AGENT' env variable if any.")
    private val httpUserAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",

    @TomlComment("themoviedb.org API Token. Will prefer 'TMDB_TOKEN' env variable if any.")
    private val tmdbToken: String = ""
) {
    fun debug() = getEnvBool("DEBUG", debug)
    fun siteBaseURL() = getEnvString("BASE_URL", siteBaseURL).removeSuffix("/")
    fun imageBaseURL() = getEnvString("IMAGE_URL", imageBaseURL).removeSuffix("/")
    fun apiBaseURL() = getEnvString("API_BASE_URL", apiBaseURL).removeSuffix("/")
    fun imageDir() = getEnvString("IMAGE_DIR", imageDir)
    fun buildDir() = getEnvString("BUILD_DIR", buildDir)
    fun androidBuildDir() = getEnvString("ANDROID_BUILD_DIR", androidBuildDir)
    fun mpvDir() = getEnvString("MPV_DIR", mpvDir)
    fun httpUserAgent() = getEnvString("USER_AGENT", httpUserAgent)
    fun tmdbToken() = getEnvString("TMDB_TOKEN", tmdbToken)
}

@Serializable
data class DatabaseConfig(
    @TomlComment("Host or IP of the database. Will prefer 'DB_HOST' env variable if any.")
    private val host: String = "",

    @TomlComment("Username for the database.  Will prefer 'DB_USER' env variable if any.")
    private val user: String = "",

    @TomlComment("Password for the database.  Will prefer 'DB_PASS' env variable if any.")
    private val pass: String = ""
) {
    fun host() = getEnvString("DB_HOST", host)
    fun user() = getEnvString("DB_USER", user)
    fun pass() = getEnvString("DB_PASS", pass)
}

@Serializable
data class DiscordConfig(
    @TomlComment(
        """
        Discord Bot Token used for the downloader to notify about new episodes.
        Will prefer 'DISCORD_TOKEN' env variable if any.
    """
    )
    private val botToken: String = "",

    @TomlComment(
        """
        Channel where some backend logs like new devices being added etc. are posted to.
        In the format: https://discord.com/channels/<ServerID>/<ChannelID>
        Will prefer 'LOG_CHANNEL_URL' env variable if any.
    """
    )
    private val logChannelURL: String = "",

    @TomlComment(
        """
        The channel announcements for new episodes will be posted in.
        In the format: https://discord.com/channels/<ServerID>/<ChannelID>
        Will prefer 'DISCORD_CHANNEL_URL' env variable if any.
    """
    )
    private val announcementChannelURL: String = "",

    @TomlComment(
        """
        The role ID that users should have if they should be pinged in the channel for new episodes of favourites.
        Will prefer 'DISCORD_PING_ROLE' env variable if any.
    """
    )
    private val announcementPingRole: String = "",

    @TomlComment(
        """
        The role ID that users should have if they should be directly messaged by the bot for new episodes of favourites.
        Will prefer 'DISCORD_DM_ROLE' env variable if any.
    """
    )
    private val announcementDmRole: String = "",

    @TomlComment(
        """
        Used by styx-web for manual imports. Should ideally be in the same channel as the one above.
        Will prefer 'DISCORD_WEBHOOK' env variable if any.
        """
    )
    private val announcementWebhookURL: String = "",

    @TomlComment(
        """
        Used by styx-dl to show the current schedule in a message.
        If this is a channel URL it will post a new message in that channel and update the config accordingly.
        Will prefer 'DISCORD_SCHEDULE_MSG' env variable if any.
        """
    )
    private val scheduleMessageURL: String = "",
    @TomlComment("Client ID used for authentication. Will prefer 'DISCORD_CLIENT_ID' env variable if any.")
    private val discordClientID: String = "",
    @TomlComment("Client Secret used for authentication. Will prefer 'DISCORD_CLIENT_SECRET' env variable if any.")
    private val discordClientSecret: String = ""
) {
    fun botToken(): String = getEnvString("DISCORD_TOKEN", botToken)
    fun logChannelURL(): String = getEnvString("LOG_CHANNEL_URL", logChannelURL)
    fun announcementChannelURL(): String = getEnvString("DISCORD_CHANNEL_URL", announcementChannelURL)
    fun announcementPingRole(): String = getEnvString("DISCORD_PING_ROLE", announcementPingRole)
    fun announcementDmRole(): String = getEnvString("DISCORD_DM_ROLE", announcementDmRole)
    fun announcementWebhookURL(): String = getEnvString("DISCORD_WEBHOOK", announcementWebhookURL)
    fun scheduleMessageURL(): String = getEnvString("DISCORD_SCHEDULE_MSG", scheduleMessageURL)
    fun discordClientID(): String = getEnvString("DISCORD_CLIENT_ID", discordClientID)
    fun discordClientSecret(): String = getEnvString("DISCORD_CLIENT_SECRET", discordClientSecret)
}

@Serializable
data class APIConfig(
    @TomlComment(
        """
        Host to bind for styx-api.
        Leaving this empty assumes 0.0.0.0 when running in a docker and localhost otherwise.
    """
    )
    private val serveHost: String = "",
    @TomlComment("Port to bind for styx-api.")
    val servePort: Int = 8081,
    @TomlComment(
        """
        Enable or disable checking the APPSECRET that clients use as verification that a build is legitimate.
        Secrets are to be put in a 'SECRETS' file in the same directory as this config.
        Just plain text and each line is a separate secret.
    """
    )
    val enableSecretsCheck: Boolean = true
) {
    fun serveHost(): String {
        if (serveHost.isNotBlank()) return serveHost
        return if (isDocker) "0.0.0.0" else "localhost"
    }
}

@Serializable
data class WebConfig(
    @TomlComment(
        """
        Host to bind for styx-web.
        Leaving this empty assumes 0.0.0.0 when running in a docker and localhost otherwise.
    """
    )
    private val serveHost: String = "",
    @TomlComment("Port to bind for styx-web.")
    val servePort: Int = 8080,
    @TomlComment("Discord Auth Token to be used, essentially skipping login. DO NOT USE ON PUBLICLY HOSTED INSTANCES.")
    val debugAuthToken: String = ""
) {
    fun serveHost(): String {
        if (serveHost.isNotBlank()) return serveHost
        return if (isDocker) "0.0.0.0" else "localhost"
    }
}