package moe.styx.common.data

data class LegacyEpisodeInfo(
    val GUID: String, val permID: String, val date: String, val name: String, val ep: String, val prevName: String?,
    val ep_name_en: String?, val ep_name_de: String?, val summary_en: String?, val summary_de: String?,
    var filePath: String, var fileSize: Double
)

data class LegacyAnimeInfo(
    val GUID: String, val name: String, val season: String, val coverURL: String?, val coverMD5: String?,
    val English: String?, val Romaji: String?, val Schedule: String?, val Synopsis: String?,
    val SynopsisDE: String?, val DbMapping: String?
)

data class LegacyMovieInfo(
    val permID: String, val date: String, val name: String, val listLink: String?, val coverURL: String?,
    val coverMD5: String?, val English: String?, val Romaji: String?, val synopsis: String?, val filePath: String,
    val fileSize: Double, val category: String
)