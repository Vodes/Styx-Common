package moe.styx.common.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.peanuuutz.tomlkt.TomlComment

@Serializable
data class DownloaderConfig(
    @TomlComment("Ignore episodes like 'One Piece - Egghead SP1' or 'One Piece E1078.5'")
    val ignoreSpecialsAndPoint5: Boolean = true,
    @TomlComment("FTP Connection used if there isn't any explicitly passed in the downloader option.")
    val defaultFTPConnectionString: String = "",
    @SerialName("RSS-Config")
    val rssConfig: RSSConfig = RSSConfig(),
    @SerialName("Torrent-Client")
    val torrentConfig: TorrentConfig = TorrentConfig(),
    @SerialName("SABnzbd-Client")
    val sabnzbdConfig: SABnzbdConfig = SABnzbdConfig(),
    @SerialName("IRC-Config")
    val ircConfig: IRCConfig = IRCConfig()
)

@Serializable
data class RSSConfig(
    @TomlComment("Directory where only temporary downloads will go to.")
    val tempDir: String = "",
    @TomlComment("Directory where downloads will be kept for seeding.")
    val seedDir: String = "",
    @TomlComment(
        """
            Templates you can use in torrent/usenet options as shortcuts for feeds to check.
            Do not include a query string in templates if you want to use dynamic queries in the webui!
            '%tosho%my hero academia' would result in 'https://feed.animetosho.org/rss2?q=my+hero+academia'
        """
    )
    val feedTemplates: Map<String, String> = mapOf("tosho" to "https://feed.animetosho.org/rss2"),
)

@Serializable
data class TorrentConfig(
    @TomlComment("Type of torrent client, can be Flood or Transmission.")
    val clientType: String = "Transmission",
    @TomlComment("URL to the client.")
    val clientURL: String = "",
    @TomlComment("Username used for the client.")
    val clientUser: String = "",
    @TomlComment("Password used for the client.")
    val clientPass: String = ""
)

@Serializable
data class SABnzbdConfig(
    @TomlComment("URL to the SABnzbd instance.")
    val sabURL: String = "",
    @TomlComment("API-Key for the instance.")
    val sabApiKey: String = ""
)

@Serializable
data class IRCConfig(
    @TomlComment("A map of server host and the channels to be listened to.")
    val servers: Map<String, List<String>> = mapOf("irc.rizon.net" to listOf("#subsplease", "#Styx-XDCC")),
    @TomlComment("Whitelisted XDCC Bots to listen for.")
    val whitelistedXDCCBots: List<String> = listOf("StyxXDCC", "CR-ARUTHA|NEW")
)