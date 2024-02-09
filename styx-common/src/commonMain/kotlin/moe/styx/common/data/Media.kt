package moe.styx.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val GUID: String, val name: String, val nameJP: String?, val nameEN: String?, val synopsisEN: String?,
    val synopsisDE: String?, val thumbID: String?, val bannerID: String? = null, val categoryID: String? = null,
    val prequel: String? = null, val sequel: String? = null, val genres: String? = null, val tags: String? = null,
    val metadataMap: String? = null, val isSeries: Int = 1, val added: Long = 0
)

@Serializable
data class Category(val GUID: String, val sort: Int, val isSeries: Int, val isVisible: Int, val name: String)

@Serializable
data class Image(
    val GUID: String, val hasWEBP: Int? = 0, val hasPNG: Int? = 0,
    val hasJPG: Int? = 0, val externalURL: String? = null, val type: Int = 0
)

@Serializable
data class MediaEntry(
    val GUID: String, val mediaID: String, val timestamp: Long, val entryNumber: String,
    val nameEN: String?, val nameDE: String?, val synopsisEN: String?, val synopsisDE: String?,
    val thumbID: String?, val filePath: String, val fileSize: Long, val originalName: String?
)

@Serializable
data class MediaInfo(
    val entryID: String, var videoCodec: String, var videoBitdepth: Int, var videoRes: String,
    var hasEnglishDub: Int, var hasGermanDub: Int, var hasGermanSub: Int
)

/**
 * Class to save progress on an entry.
 *
 * @param entryID ID for the corresponding episode or movie
 * @param userID ID for the user
 * @param lastWatched The time this was last watched (not sure what I'm gonna use this for yet)
 * @param progress Where the last playback session stopped, in seconds. This will be used to resume playback and can be reduced.
 * @param progressPercent Where the last playback session stopped, in percent
 * @param maxProgress The maximum progress reached so far, in percent. This will essentially mean that something has been watched if >80%. This can never be reduced.
 */
@Serializable
data class MediaWatched(
    val entryID: String,
    val userID: String,
    var lastWatched: Long,
    var progress: Long,
    var progressPercent: Float,
    var maxProgress: Float
)

@Serializable
data class MediaSchedule(
    var mediaID: String,
    var day: ScheduleWeekday,
    var hour: Int,
    var minute: Int,
    var isEstimated: Int = 0,
    var finalEpisodeCount: Int = 0
)

enum class ScheduleWeekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}