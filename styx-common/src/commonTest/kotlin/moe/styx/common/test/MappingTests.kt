package moe.styx.common.test

import moe.styx.common.data.BasicMapping
import moe.styx.common.data.IMapping
import moe.styx.common.data.MappingCollection
import moe.styx.common.data.TMDBMapping
import moe.styx.common.data.tmdb.StackType
import moe.styx.common.data.tmdb.getMappingForEpisode
import moe.styx.common.data.tmdb.sanitizeMappings
import moe.styx.common.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

val TENSURA_S2_MAPPING = """
    {"tmdbMappings":[{"remoteID":82684,"seasonEntry":2}],"anilistMappings":[{"matchFrom":1.0,"matchUntil":12.0,"remoteID":108511},{"matchFrom":13.0,"matchUntil":24.0,"remoteID":116742}],"malMappings":[{"matchFrom":1.0,"matchUntil":12.0,"remoteID":39551},{"matchFrom":13.0,"matchUntil":24.0,"remoteID":41487}]}
""".trimIndent()

val JOBLESS_MAPPING = """
    {"tmdbMappings":[{"remoteID":94664},{"matchFrom":17.5,"matchUntil":17.5,"remoteID":94664,"seasonEntry":0,"offset":-16.5}],"anilistMappings":[{"matchFrom":12.0,"matchUntil":23.0,"remoteID":127720,"offset":-11.0},{"matchFrom":1.0,"matchUntil":11.0,"remoteID":108465},{"matchFrom":17.5,"remoteID":141534,"offset":-16.5}],"malMappings":[{"matchFrom":12.0,"matchUntil":23.0,"remoteID":45576,"offset":-11.0},{"matchFrom":1.0,"matchUntil":11.0,"remoteID":39535},{"matchFrom":17.5,"remoteID":50360,"offset":-16.5}]}
""".trimIndent()

class MappingTests {

    @Test
    fun testTensuraMappings() {
        val mappings = json.decodeFromString<MappingCollection>(TENSURA_S2_MAPPING)
        assertTrue { mappings.anilistMappings.isNotEmpty() && mappings.malMappings.isNotEmpty() && mappings.tmdbMappings.isNotEmpty() }

        val sanitizedTMDB = mappings.sanitizeMappings<TMDBMapping>(StackType.TMDB)
        assertTrue { sanitizedTMDB?.fallback is TMDBMapping }

        val sanitizedAnilist = mappings.sanitizeMappings<BasicMapping>(StackType.ANILIST)!!
        assertEquals(-12.0, sanitizedAnilist.rangeMappings[1].offset)

        var mapping = mappings.getMappingForEpisode<BasicMapping>("03", StackType.ANILIST)
        assertEquals(108511, mapping!!.remoteID)
        mapping = mappings.getMappingForEpisode<BasicMapping>("13", StackType.ANILIST)
        assertEquals(116742, mapping!!.remoteID)
        assertEquals(1.0, "13".toDouble() + mapping.offset)
    }

    @Test
    fun testJoblessMappings() {
        val mappings = json.decodeFromString<MappingCollection>(JOBLESS_MAPPING)
        assertTrue { mappings.anilistMappings.isNotEmpty() && mappings.malMappings.isNotEmpty() && mappings.tmdbMappings.isNotEmpty() }

        val sanitizedTMDB = mappings.sanitizeMappings<TMDBMapping>(StackType.TMDB)
        assertTrue { sanitizedTMDB?.fallback is TMDBMapping }

        val sanitizedAnilist = mappings.sanitizeMappings<BasicMapping>(StackType.ANILIST)!!
        assertEquals(-11.0, sanitizedAnilist.rangeMappings[1].offset)

        var mapping = mappings.getMappingForEpisode<IMapping>("03", StackType.ANILIST)
        assertEquals(108465, mapping!!.remoteID)

        mapping = mappings.getMappingForEpisode<IMapping>("12", StackType.ANILIST)
        assertEquals(127720, mapping!!.remoteID)

        mapping = mappings.getMappingForEpisode<IMapping>("17.5", StackType.ANILIST)
        assertEquals(141534, mapping!!.remoteID)
        assertEquals(1.0, "17.5".toDouble() + mapping.offset)

        mapping = mappings.getMappingForEpisode<IMapping>("17.5", StackType.TMDB)
        assertTrue { mapping is TMDBMapping }
        assertEquals(94664, mapping!!.remoteID)
        assertEquals(1.0, "17.5".toDouble() + mapping.offset)
    }
}
