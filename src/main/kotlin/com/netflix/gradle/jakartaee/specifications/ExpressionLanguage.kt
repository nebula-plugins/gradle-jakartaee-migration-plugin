package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class ExpressionLanguage : ContainerProvidedSpecification(
    JAVAX,
    JAKARTA,
    listOf(TOMCAT, TOMCAT_EMBED_EL),
    SPECIFICATION_TO_EL_VERSION
) {
    companion object {
        private val JAVAX_2 = ArtifactCoordinate("javax.el", "el-api") // 1.0 through 2.2
        private val JAVAX = ArtifactCoordinate("javax.el", "javax.el-api") // 2.2.1 through 3.0.0 (also 1.1.2 patch release)
        private val JAKARTA = ArtifactCoordinate("jakarta.el", "jakarta.el-api") // 3.0.2 and later

        private val TOMCAT_6 = ArtifactCoordinate("org.apache.tomcat", "el-api") // 6.0.x
        private val TOMCAT = ArtifactCoordinate("org.apache.tomcat", "tomcat-el-api") // 7.0.0 and later
        private val TOMCAT_EMBED_EL = ArtifactCoordinate("org.apache.tomcat.embed", "tomcat-embed-el") // 7.0.0 and later, includes implementation

        private val SPECIFICATION_TO_EL_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("3.0"),
            SpecificationVersion.EE8 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9 to ArtifactVersion("4.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("4.0"),
            SpecificationVersion.EE10 to ArtifactVersion("5.0"),
        )
    }

    override val name: String
        get() = "el-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            TOMCAT_6,
            TOMCAT,
            JAVAX_2,
            JAVAX,
            JAKARTA,
            TOMCAT_EMBED_EL
        )
}
