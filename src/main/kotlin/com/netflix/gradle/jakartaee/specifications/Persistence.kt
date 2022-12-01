package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Persistence : BasicSpecification(
    ECLIPSE,
    JAKARTA,
    SPECIFICATION_TO_PERSISTENCE_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.persistence") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_1_0 = ArtifactCoordinate("javax.persistence", "persistence-api") // 1.0, 1.0.2
        private val ECLIPSE = ArtifactCoordinate("org.eclipse.persistence", "javax.persistence") // 2.0.0 through 2.2.1
        private val JAVAX = ArtifactCoordinate("javax.persistence", "javax.persistence-api") // 2.2
        private val JAKARTA = ArtifactCoordinate("jakarta.persistence", "jakarta.persistence-api") // 2.2.1 and later

        private val SPECIFICATION_TO_PERSISTENCE_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.1"),
            SpecificationVersion.EE8 to ArtifactVersion("2.2"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.1"),
        )
    }

    override val name: String
        get() = "persistence-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX_1_0,
            ECLIPSE,
            JAVAX,
            JAKARTA,
        )
}
