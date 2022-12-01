package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Batch : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_BATCH_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.batch", "javax.batch-api") // 1.0 through 1.0.1
        private val JAKARTA = ArtifactCoordinate("jakarta.batch", "jakarta.batch-api") // 1.0.1 and later

        private val SPECIFICATION_TO_BATCH_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.1"),
            SpecificationVersion.EE10 to ArtifactVersion("2.1"),
        )
    }

    override val name: String
        get() = "batch-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
