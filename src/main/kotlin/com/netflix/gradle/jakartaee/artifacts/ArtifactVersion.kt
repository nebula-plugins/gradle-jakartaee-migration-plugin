package com.netflix.gradle.jakartaee.artifacts

import java.util.*

internal data class ArtifactVersion(val source: String) : Comparable<ArtifactVersion> {

    // Gradle's parsing/comparison code to avoid having to refer to internal classes or take a dependency on a library
    companion object {
        private val SPECIAL_MEANINGS = mapOf(
            "dev" to -1,
            "b" to 1,
            "beta" to 1,
            "m" to 2,
            "milestone" to 2,
            "rc" to 3,
            "final" to 4,
            "release" to 5,
            "sources" to 6
        )
    }

    val parts: List<String> by lazy { buildParts() }
    val numericParts: List<Long?> = parts.map { it.toLongOrNull() }

    override fun compareTo(other: ArtifactVersion): Int {
        if (this == other) {
            return 0
        }

        val parts1 = parts
        val parts2 = other.parts
        val numericParts1 = numericParts
        val numericParts2 = other.numericParts

        var i = 0
        while (i < parts1.size && i < parts2.size) {
            val part1 = parts1[i]
            val part2 = parts2[i]

            val numericPart1 = numericParts1[i]
            val numericPart2 = numericParts2[i]

            val is1Number = numericPart1 != null
            val is2Number = numericPart2 != null

            if (part1 == part2) {
                i++
                continue
            }
            if (is1Number && !is2Number) {
                return 1
            }
            if (is2Number && !is1Number) {
                return -1
            }
            if (is1Number && is2Number) {
                val result = numericPart1!!.compareTo(numericPart2!!)
                if (result == 0) {
                    i++
                    continue
                }
                else return result
            }
            // both are strings, we compare them taking into account special meaning
            val sm1 = SPECIAL_MEANINGS[part1.lowercase(Locale.US)]
            var sm2: Int? = SPECIAL_MEANINGS[part2.lowercase(Locale.US)]
            if (sm1 != null) {
                sm2 = sm2 ?: 0
                return sm1 - sm2
            }
            return if (sm2 != null) {
                -sm2
            } else part1.compareTo(part2)
        }
        if (i < parts1.size) {
            return if (numericParts1[i] == null) -1 else 1
        }
        return if (i < parts2.size) {
            if (numericParts2[i] == null) 1 else -1
        } else source.compareTo(other.source)
    }

    private fun buildParts(): List<String> {
        val parts = mutableListOf<String>()
        var digit = false
        var startPart = 0
        var pos = 0
        while (pos < source.length) {
            when (source[pos]) {
                '.', '_', '-', '+', '~' -> {
                    parts.add(source.substring(startPart, pos))
                    startPart = pos + 1
                    digit = false
                }
                in '0'..'9' -> {
                    if (!digit && pos > startPart) {
                        parts.add(source.substring(startPart, pos))
                        startPart = pos
                    }
                    digit = true
                }
                else -> {
                    if (digit) {
                        parts.add(source.substring(startPart, pos))
                        startPart = pos
                    }
                    digit = false
                }
            }
            pos++
        }
        if (pos > startPart) {
            parts.add(source.substring(startPart, pos))
        }
        return parts
    }
}
