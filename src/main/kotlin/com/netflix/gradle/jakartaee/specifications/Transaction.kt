package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Transaction : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_TRANSACTION_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.transaction") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_1_1 = ArtifactCoordinate("javax.transaction", "transaction-api") // 1.1
        private val JAVAX = ArtifactCoordinate("javax.transaction", "javax.transaction-api") // 1.2 through 1.3
        private val JAKARTA = ArtifactCoordinate("jakarta.transaction", "jakarta.transaction-api") // 1.3.2 and later

        private val SPECIFICATION_TO_TRANSACTION_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.1"),
            SpecificationVersion.EE8 to ArtifactVersion("2.2"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.1"),
        )
    }

    override val name: String
        get() = "transaction-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX_1_1,
            JAVAX,
            JAKARTA,
        )
}
