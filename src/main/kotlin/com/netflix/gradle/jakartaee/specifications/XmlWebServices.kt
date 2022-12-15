package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class XmlWebServices : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_XML_WS_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.xml.ws") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_2 = ArtifactCoordinate("javax.xml", "jaxws-api") // 2.0 relocates to javax.xml.ws
        private val JAVAX = ArtifactCoordinate("javax.xml.ws", "jaxws-api") // 2.0 through 2.3.1
        private val JAKARTA = ArtifactCoordinate("jakarta.xml.ws", "jakarta.xml.ws-api") // 2.3.2 and later

        private val SPECIFICATION_TO_XML_WS_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.2"),
            SpecificationVersion.EE8 to ArtifactVersion("2.3"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0"),
        )
    }

    override val name: String
        get() = "xml.ws-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX_2,
            JAVAX,
            JAKARTA,
        )
}
