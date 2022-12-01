package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class JavaBeans : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_EJB_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.ejb") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.ejb", "ejb") // 2.0 through 2.1
        private val JAVAX_3_0 = ArtifactCoordinate("javax.ejb", "ejb-api") // 3.0
        private val JAVAX_3_2 = ArtifactCoordinate("javax.ejb", "javax.ejb-api") // 3.2 through 3.2.2
        private val JAKARTA = ArtifactCoordinate("jakarta.ejb", "jakarta.ejb-api") // 3.2.3 and later

        private val SPECIFICATION_TO_EJB_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("3.2"),
            SpecificationVersion.EE8 to ArtifactVersion("3.2"),
            SpecificationVersion.EE9 to ArtifactVersion("4.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("4.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0"),
        )
    }

    override val name: String
        get() = "ejb-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAVAX_3_0,
            JAVAX_3_2,
            JAKARTA,
        )
}
