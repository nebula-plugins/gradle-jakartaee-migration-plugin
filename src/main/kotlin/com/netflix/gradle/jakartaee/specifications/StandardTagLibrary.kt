package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class StandardTagLibrary : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_STL_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.servlet.jsp.jstl") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_LEGACY = ArtifactCoordinate("jstl", "jstl") // 1.0.3 through 1.2
        private val JAVAX_1_2 = ArtifactCoordinate("javax.servlet.jsp.jstl", "jstl") // 1.2
        private val JAVAX = ArtifactCoordinate("javax.servlet.jsp.jstl", "javax.servlet.jsp.jstl-api") // 1.2.1, 1.2.2
        private val JAKARTA = ArtifactCoordinate("jakarta.servlet.jsp.jstl", "jakarta.servlet.jsp.jstl-api") // 1.2.3 and later

        private val SPECIFICATION_TO_STL_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.2"),
            SpecificationVersion.EE8 to ArtifactVersion("1.2"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"),
        )
    }

    override val name: String
        get() = "jsp.jstl-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX_LEGACY,
            JAVAX_1_2,
            JAVAX,
            JAKARTA,
        )
}
