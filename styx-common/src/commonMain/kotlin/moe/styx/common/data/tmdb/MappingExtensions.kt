package moe.styx.common.data.tmdb

import moe.styx.common.data.IMapping
import moe.styx.common.data.MappingCollection
import moe.styx.common.data.Media
import moe.styx.common.json
import moe.styx.common.util.Log

fun Media.decodeMapping(): MappingCollection? {
    return this.metadataMap?.let {
        if (it.isBlank())
            return null
        else
            return runCatching { json.decodeFromString<MappingCollection>(it) }.getOrNull()
    }
}

fun MappingCollection?.orEmpty() = this ?: MappingCollection()

fun Media.getFirstIDFromMap(type: StackType): Int? {
    val mappingJson = decodeMapping() ?: return null
    return when (type) {
        StackType.TMDB -> mappingJson.tmdbMappings.minByOrNull { it.remoteID }?.remoteID
        StackType.MAL -> mappingJson.malMappings.minByOrNull { it.remoteID }?.remoteID
        else -> mappingJson.anilistMappings.minByOrNull { it.remoteID }?.remoteID
    }
}

fun Media.getFirstTMDBSeason(): Int? {
    val mappingJson = decodeMapping() ?: return null
    return mappingJson.tmdbMappings.minByOrNull { it.seasonEntry }?.seasonEntry
}

data class SplitMappings<out T : IMapping>(val fallback: T?, val rangeMappings: List<T>, val epMappings: List<T>) {

    /**
     * Get the best possible matching Mapping for an episode.
     *
     * In priority of episode-specific > range > fallback.
     *
     * @param episode Episode number as string. Should be something that can be parsed as a double.
     * @param allowZero Whether to allow an episode 0. This may be interesting to disable for movies and/or specials.
     *
     * @return Returns a fitting match among the mappings if any.
     */
    fun getMappingForEpisode(
        episode: String,
        allowZero: Boolean = true
    ): T? {
        val epNumber = (episode.toDoubleOrNull() ?: -1.0).let {
            if (!allowZero && it == 0.0)
                1.0
            else it
        }

        var match = epMappings.find { it.matchFrom == epNumber }
        if (match == null) {
            match = rangeMappings.find { epNumber >= it.matchFrom && epNumber <= it.matchUntil }
        }

        return match ?: fallback
    }
}

/**
 * Sanitizes and splits mappings into something usable for actual episode selection later on.
 *
 * @param type  Type of mappings to work with.
 *
 * @return A Triple of fallback-mapping (can be null), list of range mappings and list of episode specific mappings.
 *         Can be null if no Mappings exist.
 */
inline fun <reified T : IMapping> MappingCollection.sanitizeMappings(type: StackType): SplitMappings<T>? {
    val mappings = when (type) {
        StackType.TMDB -> this.tmdbMappings
        StackType.ANILIST -> this.anilistMappings
        else -> this.malMappings
    }
    if (mappings.isEmpty()) return null

    // Mapping that should apply to everything that doesn't meet the requirements of the others
    val fallbackMapping = mappings.find { it.matchFrom == -1.0 && it.matchUntil == -1.0 }
    // Mapping that should apply for episode numbers in a given range
    var rangeMappings = mappings.filter { it.matchFrom != -1.0 && it.matchUntil != -1.0 && it.matchFrom != it.matchUntil }
    // Mappings that should apply for specific episodes (until == from or until == -1 and from != -1)
    val specificEpisodeMappings = mappings.filter { it.matchFrom != -1.0 && (it.matchFrom == it.matchUntil || it.matchUntil == -1.0) }

    // Sanitize mappings that specify episode ranges for everything but where I might've forgotten to set an offset.
    if (fallbackMapping == null && rangeMappings.size > 1 && rangeMappings.all { it.offset == 0.0 }) {
        val sorted = rangeMappings.sortedBy { it.matchFrom }.toMutableList()
        sorted.forEachIndexed { i, _ ->
            if (i == 0)
                return@forEachIndexed
            sorted[i].offset = -(sorted[i - 1].matchUntil)
        }
        rangeMappings = sorted.toList()
    }

    val castedFallbackMapping = runCatching {
        fallbackMapping?.let { it as T }
    }.onFailure {
        Log.e(null, it) { "Failed to cast fallback mapping \"${fallbackMapping.toString()}\" to requested data type." }
    }.getOrNull()

    val castedRangeMappings = runCatching { rangeMappings.sortedBy { it.matchFrom }.map { it as T } }.onFailure {
        Log.e(null, it) { "Failed to cast range mappings to requested data type." }
    }.getOrNull() ?: return null

    val castedEPRangeMappings = runCatching { specificEpisodeMappings.sortedBy { it.matchFrom }.map { it as T } }.onFailure {
        Log.e(null, it) { "Failed to cast ep mappings to requested data type." }
    }.getOrNull() ?: return null

    return SplitMappings(castedFallbackMapping, castedRangeMappings, castedEPRangeMappings)
}

/**
 * Get the best possible matching Mapping for an episode.
 *
 * In priority of episode-specific > range > fallback.
 *
 * @param episode Episode number as string. Should be something that can be parsed as a double.
 * @param type Type of mappings to work with.
 * @param allowZero Whether to allow an episode 0. This may be interesting to disable for movies and/or specials.
 *
 * @return Returns a fitting match among the mappings if any.
 */
inline fun <reified T : IMapping> MappingCollection.getMappingForEpisode(
    episode: String,
    type: StackType,
    allowZero: Boolean = true
): T? {
    val mappings = sanitizeMappings<T>(type) ?: return null
    return mappings.getMappingForEpisode(episode, allowZero)
}