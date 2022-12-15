package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Management : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_MANAGEMENT_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.management.j2ee") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX =
            ArtifactCoordinate("javax.management.j2ee", "javax.management.j2ee-api") // 1.1 through 1.1.2
        private val JAKARTA =
            ArtifactCoordinate("jakarta.management.j2ee", "jakarta.management.j2ee-api") // 1.1.3 and later

        private val SPECIFICATION_TO_MANAGEMENT_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.1"),
            SpecificationVersion.EE8 to ArtifactVersion("1.1"),
            // Optional APIs removed from the BOM for 9.0.0 and later, use last released version
            SpecificationVersion.EE9 to ArtifactVersion("1.1"),
            SpecificationVersion.EE9_1 to ArtifactVersion("1.1"),
            SpecificationVersion.EE10 to ArtifactVersion("1.1"),
        )
    }

    override val name: String
        get() = "management.j2ee-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAKARTA,
        )
}
