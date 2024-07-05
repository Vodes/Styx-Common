package moe.styx.common.data

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import moe.styx.common.extension.eqI

enum class SourceType {
    LOCAL,
    FTP,
    TORRENT,
    XDCC
}


@Serializable
/**
 * Defines any processing options you might do before importing a file.
 * Most if not all of these options are done through an external muxtools script.
 *
 * @param keepVideoOfPrevious   Keep video of previous source and discard the new video
 * @param keepAudioOfPrevious   Keep audio of previous source and discard the new audio
 * @param keepBetterAudio       Automatically determine better audio and use whatever it is
 * @param manualAudioSync       Delay to apply to audio in ms
 * @param manualSubSync         Delay to apply to subtitles in ms
 * @param removeNewSubs         Remove the subs of the new source
 * @param keepSubsOfPrevious    Keep all subtitles of previous source
 * @param keepNonEnglish        Keep all non-english subtitles of previous source
 * @param sushiSubs             Automatically sync subtitles using sushi
 * @param tppSubs               Apply the Timing-Post-Processor to subtitles
 * @param tppStyles             What styles to apply tpp to
 * @param restyleSubs           Restyle subs to the GJM Gandhi Preset
 * @param fixTagging            Apply tag fixing for various fuckups some groups might do
 * */
data class ProcessingOptions(
    val keepVideoOfPrevious: Boolean = false,
    val keepAudioOfPrevious: Boolean = false,
    val keepBetterAudio: Boolean = false,
    val manualAudioSync: Long = 0,
    val manualSubSync: Long = 0,
    val removeNewSubs: Boolean = false,
    val keepSubsOfPrevious: Boolean = false,
    val keepNonEnglish: Boolean = false,
    val sushiSubs: Boolean = false,
    val tppSubs: Boolean = false,
    val tppStyles: String = "default,main,alt,flashback,top,italic",
    val restyleSubs: Boolean = false,
    val fixTagging: Boolean = true,
)

@Serializable
data class DownloadableOption(
    val priority: Int = 0,
    val fileRegex: String,
    val source: SourceType = SourceType.LOCAL,
    val rssRegex: String? = null,
    val sourcePath: String? = null,
    val ftpConnectionString: String? = null,
    val keepSeeding: Boolean = false,
    val episodeOffset: Int? = 0,
    val ignoreDelay: Boolean = false,
    val waitForPrevious: Boolean = false,
    val addToLegacyDatabase: Boolean = false,
    val overrideNamingTemplate: String? = null,
    val overrideTitleTemplate: String? = null,
    val processingOptions: ProcessingOptions? = null,
    val commandAfter: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other !is DownloadableOption)
            return super.equals(other)
        return priority == other.priority && fileRegex eqI other.fileRegex
    }

    override fun hashCode(): Int {
        var result = priority
        result = 31 * result + fileRegex.hashCode()
        result = 31 * result + source.hashCode()
        return result
    }
}

data class DownloaderTarget(
    val mediaID: String,
    var options: MutableList<DownloadableOption> = mutableListOf(),
    val namingTemplate: String = "%group_b% %english% - S01%ep_e% (CR WEB-DL 1080p)",
    val titleTemplate: String = "%english% - S01%ep_e%",
    val outputDir: String = "/var/Anime/${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year}/%english%"
)