package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Concurrent : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_CONCURRENT_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.enterprise.concurrent", "javax.enterprise.concurrent-api") // 1.0, 1.1
        private val JAKARTA = ArtifactCoordinate("jakarta.enterprise.concurrent", "jakarta.enterprise.concurrent-api") // 1.1.1 and later

        private val SPECIFICATION_TO_CONCURRENT_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.1"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"),
        )
    }

    override val name: String
        get() = "enterprise.concurrent-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
