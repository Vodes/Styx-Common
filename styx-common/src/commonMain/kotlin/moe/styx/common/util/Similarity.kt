package moe.styx.common.util

import com.aallam.similarity.Cosine
import com.aallam.similarity.NormalizedLevenshtein

private val cos = Cosine(3)
private val normLev = NormalizedLevenshtein()

fun String?.isClose(s: String): Boolean {
    if (!this.isNullOrEmpty()) {
        val levScore = normLev.similarity(this, s)
        val cosineScore = cos.similarity(this, s)
        val avgScore = (levScore + cosineScore) / 2

        if (this.startsWith(s, true) ||
            this.equals(s, true) ||
            kotlin.math.max(cosineScore, avgScore) >= 0.3
        ) {
            return true
        }
    }
    return false
}
