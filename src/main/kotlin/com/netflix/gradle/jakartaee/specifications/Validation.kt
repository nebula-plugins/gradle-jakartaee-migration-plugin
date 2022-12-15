package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Validation : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_VALIDATION_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.validation", "validation-api") // 1.0.Beta3 through 2.0.1.Final
        private val JAKARTA = ArtifactCoordinate("jakarta.validation", "jakarta.validation-api") // 2.0.1 and later

        private val SPECIFICATION_TO_VALIDATION_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.1"),
            SpecificationVersion.EE8 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"),
        )
    }

    override val name: String
        get() = "validation-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
