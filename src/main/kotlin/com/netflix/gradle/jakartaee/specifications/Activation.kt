package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Activation : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_ACTIVATION_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.activation") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_1 = ArtifactCoordinate("javax.activation", "activation") // 1.0.2 through 1.1.1. jaf:activation relocates here
        private val SUN_JAVAX = ArtifactCoordinate("com.sun.activation", "javax.activation-api") // 1.2.0
        private val JAVAX = ArtifactCoordinate("javax.activation", "javax.activation-api") // 1.2.0
        private val JAKARTA_SUN = ArtifactCoordinate("com.sun.activation", "jakarta.activation") // 1.2.1 through 2.0.1
        private val JAKARTA = ArtifactCoordinate("jakarta.activation", "jakarta.activation-api") // 1.2.1 and later

        private val SPECIFICATION_TO_ACTIVATION_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.1"), // Inferred from com.sun.mail:javax.mail:1.5.0
            SpecificationVersion.EE8 to ArtifactVersion("1.1"), // Inferred from com.sun.mail:javax.mail:1.6.0
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.1"),
        )
    }

    override val name: String
        get() = "activation-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX_1,
            SUN_JAVAX,
            JAVAX,
            JAKARTA_SUN,
            JAKARTA,
        )
}
