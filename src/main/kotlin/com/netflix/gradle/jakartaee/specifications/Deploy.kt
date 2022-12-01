package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Deploy : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_DEPLOY_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.enterprise.deploy") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.enterprise.deploy", "javax.enterprise.deploy-api") // 1.6, 1.7
        private val JAKARTA = ArtifactCoordinate("jakarta.enterprise.deploy", "jakarta.enterprise.deploy-api") // 1.7.1 and later

        private val SPECIFICATION_TO_DEPLOY_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.6"),
            SpecificationVersion.EE8 to ArtifactVersion("1.7"),
            // Optional APIs removed from the BOM for 9.0.0 and later, use last released version
            SpecificationVersion.EE9 to ArtifactVersion("1.7"),
            SpecificationVersion.EE9_1 to ArtifactVersion("1.7"),
            SpecificationVersion.EE10 to ArtifactVersion("1.7"),
        )
    }

    override val name: String
        get() = "enterprise.deploy-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAKARTA,
        )
}
