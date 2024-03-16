package moe.styx.common.data.tmdb

enum class StackType(val displayName: String, val key: String) {
    ANILIST("Anilist", "anilist"),
    TMDB("TMDB", "tmdb"),
    MAL("MyAnimeList", "mal")
}