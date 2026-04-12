package moe.styx.common.data.tmdb

import moe.styx.common.data.IMapping
import moe.styx.common.data.MappingCollection
import moe.styx.common.data.Media
import moe.styx.common.data.TMDBMapping
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

/**
 * Sanitizes and splits mappings into something usable for actual episode selection later on.
 *
 * @param type  Type of mappings to work with.
 *
 * @return A Triple of fallback-mapping (can be null), list of range mappings and list of episode specific mappings.
 *         Can be null if no Mappings exist.
 */
inline fun <reified T : IMapping> MappingCollection.sanitizeMappings(type: StackType = StackType.TMDB): Triple<T?, List<T>, List<T>>? {
    val mappings = when (type) {
        StackType.TMDB -> this.tmdbMappings
        StackType.ANILIST -> this.anilistMappings
        else -> this.malMappings
    }
    if (mappings.isEmpty()) return null
    if (T::class == TMDBMapping::class && type != StackType.TMDB) {
        Log.w { "You shouldn't request TMDBMapping for stacktype ${type.name}!" }
    }

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

    val castedRangeMappings = runCatching { rangeMappings.map { it as T } }.onFailure {
        Log.e(null, it) { "Failed to cast range mappings to requested data type." }
    }.getOrNull() ?: return null

    val castedEPRangeMappings = runCatching { specificEpisodeMappings.map { it as T } }.onFailure {
        Log.e(null, it) { "Failed to cast ep mappings to requested data type." }
    }.getOrNull() ?: return null

    return Triple(castedFallbackMapping, castedRangeMappings, castedEPRangeMappings)
}

inline fun <reified T : IMapping> MappingCollection.getMappingForEpisode(
    episode: String,
    type: StackType = StackType.TMDB,
    allowZero: Boolean = true
): T? {
    val mappings = sanitizeMappings<T>(type) ?: return null
    val (fallback, rangeMappings, epMappings) = mappings

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