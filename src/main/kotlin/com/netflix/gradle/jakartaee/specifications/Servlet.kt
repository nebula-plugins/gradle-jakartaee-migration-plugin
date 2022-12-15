package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class Servlet : ContainerProvidedSpecification(
    JAVAX,
    JAKARTA,
    listOf(TOMCAT, TOMCAT_EMBED),
    SPECIFICATION_TO_SERVLET_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.servlet") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_2 = ArtifactCoordinate("javax.servlet", "servlet-api") // 2.2 through 2.5, also 3.0-alpha-1
        private val JAVAX = ArtifactCoordinate("javax.servlet", "javax.servlet-api") // 3.0.1 through 4.0.1
        private val JAKARTA = ArtifactCoordinate("jakarta.servlet", "jakarta.servlet-api") // 4.0.2 and later

        private val TOMCAT_5 = ArtifactCoordinate("tomcat", "servlet-api") // 5.0.x though 5.5.x
        private val TOMCAT_6 = ArtifactCoordinate("org.apache.tomcat", "servlet-api") // 6.0.x
        private val TOMCAT = ArtifactCoordinate("org.apache.tomcat", "tomcat-servlet-api") // 7.0.0 and later

        private val SPECIFICATION_TO_SERVLET_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("3.1"),
            SpecificationVersion.EE8 to ArtifactVersion("4.0"),
            SpecificationVersion.EE9 to ArtifactVersion("5.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("5.0"),
            SpecificationVersion.EE10 to ArtifactVersion("6.0"),
        )
    }

    override val name: String
        get() = "servlet-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            TOMCAT_5,
            TOMCAT_6,
            TOMCAT,
            JAVAX_2,
            JAVAX,
            JAKARTA,
            TOMCAT_EMBED
        )
}
