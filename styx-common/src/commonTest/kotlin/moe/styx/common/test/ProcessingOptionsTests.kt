package moe.styx.common.test

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import moe.styx.common.data.ProcessingOptions
import moe.styx.common.json
import moe.styx.common.toml
import kotlin.test.*

class ProcessingOptionsTests {
    @Test
    fun legacyOptionsPreserveValuesAndDefaultNewFields() {
        val fromJson = json.decodeFromString<ProcessingOptions>("""{"keepAudioOfPrevious":true,"sushiSubs":true,"tppStyles":"main"}""")
        val fromToml = toml.decodeFromString<ProcessingOptions>("keepAudioOfPrevious = true\nsushiSubs = true\ntppStyles = \"main\"\n")
        assertEquals(fromJson, fromToml)
        assertTrue(fromJson.keepAudioOfPrevious)
        assertTrue(fromJson.sushiSubs)
        assertEquals("main", fromJson.tppStyles)
        assertFalse(fromJson.fillAudioOfPrevious)
        assertFalse(fromJson.normalizeTrackNames)
        assertEquals("en,de", fromJson.restyleLanguages)
    }

    @Test
    fun newOptionsRoundTripInBothFormats() {
        val options = ProcessingOptions(fillAudioOfPrevious = true, normalizeTrackNames = true, restyleLanguages = "en,fr")
        assertEquals(options, json.decodeFromString<ProcessingOptions>(json.encodeToString(options)))
        assertEquals(options, toml.decodeFromString<ProcessingOptions>(toml.encodeToString(options)))
    }
}
