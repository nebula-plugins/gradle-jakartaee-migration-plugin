package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class RestWebServices : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_RS_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.ws.rs", "javax.ws.rs-api") // 2.0 through 2.1.1
        private val JAKARTA = ArtifactCoordinate("jakarta.ws.rs", "jakarta.ws.rs-api") // 2.1.2 and later

        private val SPECIFICATION_TO_RS_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.0"),
            SpecificationVersion.EE8 to ArtifactVersion("2.1"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.1"),
        )
    }

    override val name: String
        get() = "ws.rs-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
