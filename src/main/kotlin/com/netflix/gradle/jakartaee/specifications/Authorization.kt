package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Authorization : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_AUTHORIZATION_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.security.jacc") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.security.jacc", "javax.security.jacc-api") // 1.4 through 1.6
        private val JAKARTA_JACC = ArtifactCoordinate("jakarta.security.jacc", "jakarta.security.jacc-api") // 1.6.1
        private val JAKARTA = ArtifactCoordinate("jakarta.authorization", "jakarta.authorization-api") // 1.5.0, 2.0.0 and later

        private val SPECIFICATION_TO_AUTHORIZATION_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.5"),
            SpecificationVersion.EE8 to ArtifactVersion("1.5"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.1"),
        )
    }

    override val name: String
        get() = "authorization-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAKARTA_JACC,
            JAKARTA,
        )
}
