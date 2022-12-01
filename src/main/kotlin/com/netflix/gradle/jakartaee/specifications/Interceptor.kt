package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

class Interceptor : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_INTERCEPTOR_VERSION
) {
    companion object {
        private val JBOSS = ArtifactCoordinate("org.jboss.spec.javax.interceptor", "jboss-interceptors-api_1.1_spec") // 1.0.0.Beta1 through 1.0.1.Final
        private val JAVAX = ArtifactCoordinate("javax.interceptor", "javax.interceptor-api") // 1.2 through 1.2.2
        private val JAKARTA = ArtifactCoordinate("jakarta.interceptor", "jakarta.interceptor-api") // 1.2.3 and later

        private val SPECIFICATION_TO_INTERCEPTOR_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.2"),
            SpecificationVersion.EE8 to ArtifactVersion("1.2"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.1"),
        )
    }

    override val name: String
        get() = "interceptor-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JBOSS,
            JAVAX,
            JAKARTA,
        )
}
