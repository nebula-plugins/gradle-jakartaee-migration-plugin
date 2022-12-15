package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Security : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_SECURITY_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.security.enterprise", "javax.security.enterprise-api") // 1.0
        private val JAKARTA = ArtifactCoordinate("jakarta.security.enterprise", "jakarta.security.enterprise-api") // 1.0.1 and later

        private val SPECIFICATION_TO_SECURITY_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"),
        )
    }

    override val name: String
        get() = "security.enterprise-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
