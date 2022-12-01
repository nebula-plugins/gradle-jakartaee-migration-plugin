package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class JsonBind : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_JSONB_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.json.bind", "javax.json.bind-api") // 1.0
        private val JAKARTA = ArtifactCoordinate("jakarta.json.bind", "jakarta.json.bind-api") // 1.0.1 and later

        private val SPECIFICATION_TO_JSONB_VERSION = mapOf(
            SpecificationVersion.EE8 to ArtifactVersion("1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"),
        )
    }

    override val name: String
        get() = "json.bind-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
