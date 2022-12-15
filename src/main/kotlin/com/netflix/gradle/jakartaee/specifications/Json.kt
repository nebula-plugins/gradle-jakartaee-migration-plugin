package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactType
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Json : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_JSON_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.json", "javax.json-api") // 1.0 through 1.1.4
        private val JAVAX_GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.json") // Bundle. 1.0 through 1.1.4
        private val JAKARTA = ArtifactCoordinate("jakarta.json", "jakarta.json-api") // 1.1.5 and later
        private val JAKARTA_GLASSFISH = ArtifactCoordinate("org.glassfish", "jakarta.json") // Bundle. 1.1.5 and later

        private val SPECIFICATION_TO_JSON_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.1"),
        )
    }

    override val name: String
        get() = "json-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAVAX_GLASSFISH,
            JAKARTA,
            JAKARTA_GLASSFISH,
        )

    override fun artifactType(artifactCoordinate: ArtifactCoordinate): ArtifactType {
        if (artifactCoordinate.group == "org.glassfish") {
            return ArtifactType.BUNDLE
        }
        return super.artifactType(artifactCoordinate)
    }
}
