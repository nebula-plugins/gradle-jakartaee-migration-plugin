package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class XmlRpc : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_XML_RPC_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.xml.rpc") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.xml.rpc", "javax.xml.rpc-api") // 1.1 through 1.1.2
        private val JAKARTA = ArtifactCoordinate("jakarta.xml.rpc", "jakarta.xml.rpc-api") // 1.1.3 and later

        private val SPECIFICATION_TO_XML_RPC_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.1"),
            SpecificationVersion.EE8 to ArtifactVersion("1.1"),
            // Optional APIs removed from the BOM for 9.0.0 and later, use last released version
            SpecificationVersion.EE9 to ArtifactVersion("1.1"),
            SpecificationVersion.EE9_1 to ArtifactVersion("1.1"),
            SpecificationVersion.EE10 to ArtifactVersion("1.1"),
        )
    }

    override val name: String
        get() = "xml.rpc-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAKARTA,
        )
}
