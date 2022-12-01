package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Resource : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_RESOURCE_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.resource") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.resource","javax.resource-api") // 1.7 through 1.7.1
        private val JAKARTA = ArtifactCoordinate("jakarta.resource","jakarta.resource-api") // 1.7.2 and later

        private val SPECIFICATION_TO_RESOURCE_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.7"),
            SpecificationVersion.EE8 to ArtifactVersion("1.7"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.1"),
        )
    }

    override val name: String
        get() = "resource-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAKARTA,
        )
}
