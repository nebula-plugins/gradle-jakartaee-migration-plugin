package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Inject : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_INJECT_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.inject", "javax.inject") // 1
        private val JAKARTA = ArtifactCoordinate("jakarta.inject", "jakarta.inject-api") // 1.0 and later

        private val SPECIFICATION_TO_INJECT_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1"),
            SpecificationVersion.EE8 to ArtifactVersion("1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.0"),
        )
    }

    override val name: String
        get() = "inject-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
