package moe.styx.common.data.tmdb

import moe.styx.common.data.IMapping
import moe.styx.common.data.MappingCollection
import moe.styx.common.data.Media
import moe.styx.common.json

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

fun MappingCollection.getMappingForEpisode(episode: String, type: StackType = StackType.TMDB): IMapping? {
    val mappings = when (type) {
        StackType.TMDB -> this.tmdbMappings
        StackType.ANILIST -> this.anilistMappings
        else -> this.malMappings
    }
    var fallback: IMapping? = null
    for (mapping in mappings) {
        if (mapping.matchFrom > 0 && mapping.matchUntil > 0) {
            val epDouble = episode.toDouble()
            if (mapping.matchFrom == mapping.matchUntil) {
                if (epDouble == mapping.matchFrom)
                    return mapping
            } else {
                if (epDouble >= mapping.matchFrom && epDouble <= mapping.matchUntil)
                    return mapping
            }
        } else
            fallback = mapping
    }
    return fallback
}