package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class WebSocket : ContainerProvidedSpecification(
    JAVAX,
    JAKARTA,
    listOf(TOMCAT, TOMCAT_EMBED_WS),
    SPECIFICATION_TO_WS_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.websocket", "javax.websocket-api") // 1.0 through 1.1
        private val JAKARTA = ArtifactCoordinate("jakarta.websocket", "jakarta.websocket-api") // 1.1.1 and later

        private val TOMCAT = ArtifactCoordinate("org.apache.tomcat", "tomcat-websocket-api") // 7.0.0 and later
        private val TOMCAT_EMBED_WS =
            ArtifactCoordinate("org.apache.tomcat.embed", "tomcat-embed-websocket") // 7.0.0 and later

        private val SPECIFICATION_TO_WS_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("4.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0"),
        )
    }

    override val name: String
        get() = "websocket-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            TOMCAT,
            JAVAX,
            JAKARTA,
            TOMCAT_EMBED_WS
        )
}
