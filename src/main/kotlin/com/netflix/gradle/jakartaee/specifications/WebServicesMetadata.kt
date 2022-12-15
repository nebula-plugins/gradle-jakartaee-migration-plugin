package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class WebServicesMetadata : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_XML_WS_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.jws") // Repackaged OSGi bundle, appears to be EE 6
        private val JSR181 = ArtifactCoordinate("javax.jws", "jsr181") // 1.0
        private val JSR181_API = ArtifactCoordinate("javax.jws", "jsr181-api") // 1.0-MR1
        private val JAVAX = ArtifactCoordinate("javax.jws", "javax.jws-api") // 1.1
        private val JAKARTA = ArtifactCoordinate("jakarta.jws", "jakarta.jws-api") // 1.1.1 and later

        private val SPECIFICATION_TO_XML_WS_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.1"), // Inferrred from first jakarta release
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"), // Current version, no longer in API
        )
    }

    override val name: String
        get() = "jws-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JSR181,
            JSR181_API,
            JAVAX,
            JAKARTA,
        )
}
