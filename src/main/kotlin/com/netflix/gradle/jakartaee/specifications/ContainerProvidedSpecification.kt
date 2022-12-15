package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.*
import java.lang.IllegalArgumentException

abstract internal class ContainerProvidedSpecification(
    javaxCoordinate: ArtifactCoordinate,
    jakartaCoordinate: ArtifactCoordinate,
    private val tomcatCoordinates: List<ArtifactCoordinate>,
    private val specificationToImplementationVersion: Map<SpecificationVersion, ArtifactVersion>
) : BasicSpecification(javaxCoordinate, jakartaCoordinate, specificationToImplementationVersion) {
    companion object {
        internal val TOMCAT_TO_EE_VERSION = mapOf(
            ArtifactVersion("8.0") to SpecificationVersion.EE7,
            ArtifactVersion("8.5") to SpecificationVersion.EE7,
            ArtifactVersion("9.0") to SpecificationVersion.EE8,
            ArtifactVersion("10.0") to SpecificationVersion.EE9,
            ArtifactVersion("10.1") to SpecificationVersion.EE10
        )

        private val EE_TO_TOMCAT_VERSION = TOMCAT_TO_EE_VERSION.entries
            .associateBy({ it.value }) { it.key }

        internal val TOMCAT_EMBED =
            ArtifactCoordinate("org.apache.tomcat.embed", "tomcat-embed-core") // 7.0.0 and later
    }

    override fun implementationsForSpecification(specificationVersion: SpecificationVersion): List<ArtifactVersionCoordinate> {
        val tomcatVersion = EE_TO_TOMCAT_VERSION[specificationVersion]!!
        return super.implementationsForSpecification(specificationVersion) +
                tomcatCoordinates.map { it.withVersion(tomcatVersion.source) }
    }

    override fun implementationVersionFor(artifactVersion: ArtifactVersionCoordinate): ArtifactVersion {
        var implementationVersion: ArtifactVersion = super.implementationVersionFor(artifactVersion)
        val group = artifactVersion.module.group
        if (group.startsWith("tomcat") || group.startsWith("org.apache.tomcat")) {
            val specificationVersion = TOMCAT_TO_EE_VERSION[implementationVersion]
                ?: throw IllegalArgumentException("No specification version found for Tomcat $implementationVersion")
            implementationVersion = specificationToImplementationVersion[specificationVersion]!!
        }
        return implementationVersion
    }

    override fun artifactType(artifactCoordinate: ArtifactCoordinate): ArtifactType {
        return if (artifactCoordinate.group == "org.apache.tomcat.embed") {
            ArtifactType.BUNDLE
        } else ArtifactType.API
    }
}
