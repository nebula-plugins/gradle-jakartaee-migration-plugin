package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class MessageService : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_CDI_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.jms") // Repackaged OSGi bundle, appears to be EE 6
        private val JMS = ArtifactCoordinate("jms", "jms") // 1.0.2. 1.1 relocates to javax.jms:jms
        private val JMS_1_1 = ArtifactCoordinate("javax.jms", "jms") // 1.1
        private val JAVAX = ArtifactCoordinate("javax.jms", "javax.jms-api") // 2.0 through 2.0.1
        private val JAKARTA = ArtifactCoordinate("jakarta.jms", "jakarta.jms-api") // 2.0.2 and later

        private val SPECIFICATION_TO_CDI_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.0"),
            SpecificationVersion.EE8 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.1"),
        )
    }

    override val name: String
        get() = "jms-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JMS,
            JMS_1_1,
            JAVAX,
            JAKARTA,
        )
}
