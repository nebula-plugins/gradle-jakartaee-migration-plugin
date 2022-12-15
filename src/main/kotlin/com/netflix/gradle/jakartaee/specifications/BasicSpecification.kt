package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.*
import java.lang.IllegalArgumentException

internal abstract class BasicSpecification(
    private val javaxCoordinate: ArtifactCoordinate,
    private val jakartaCoordinate: ArtifactCoordinate,
    private val specificationToImplementationVersion: Map<SpecificationVersion, ArtifactVersion>
) : Specification {
    override fun implementationsForSpecification(specificationVersion: SpecificationVersion): List<ArtifactVersionCoordinate> {
        val defaultImplementation =
            if (specificationVersion <= SpecificationVersion.EE8) javaxCoordinate else jakartaCoordinate
        val version = specificationToImplementationVersion[specificationVersion]!!.toString()
        return listOf(defaultImplementation.withVersion(version))
    }

    override fun implementationVersionFor(artifactVersion: ArtifactVersionCoordinate): ArtifactVersion {
        if (artifactVersion.module.group == "org.glassfish") {
            return specificationToImplementationVersion[SpecificationVersion.EE7]!!
        }
        return artifactVersion.version.minorVersion
    }

    override fun artifactType(artifactCoordinate: ArtifactCoordinate) =
        ArtifactType.API
}
