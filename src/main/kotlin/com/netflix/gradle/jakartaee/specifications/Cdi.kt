package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Cdi : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_CDI_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.enterprise", "cdi-api") // 1.0 through 2.0
        private val JAKARTA_ORPHANED = ArtifactCoordinate("jakarta.enterprise", "cdi-api") // 3.0.0-M3
        private val JAKARTA = ArtifactCoordinate("jakarta.enterprise", "jakarta.enterprise.cdi-api") // 2.0.1 and later

        private val SPECIFICATION_TO_CDI_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.1"),
            SpecificationVersion.EE8 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0"),
        )
    }

    override val name: String
        get() = "enterprise.cdi-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA_ORPHANED,
            JAKARTA,
        )
}
