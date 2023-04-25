/*
 * Copyright 2022 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.*

internal abstract class BasicSpecification(
    final override val name: String,
    private val javaxCoordinate: ArtifactCoordinate,
    final override val javaxCoordinates: List<ArtifactCoordinate>,
    private val jakartaCoordinate: ArtifactCoordinate,
    final override val jakartaCoordinates: List<ArtifactCoordinate>,
    private val specificationToImplementationVersion: Map<SpecificationVersion, ArtifactVersion>
) : Specification {

    constructor(
        name: String,
        javaxCoordinate: ArtifactCoordinate,
        jakartaCoordinate: ArtifactCoordinate,
        specificationToImplementationVersion: Map<SpecificationVersion, ArtifactVersion>
    ) : this(
        name,
        javaxCoordinate,
        listOf(javaxCoordinate),
        jakartaCoordinate,
        listOf(jakartaCoordinate),
        specificationToImplementationVersion
    )

    private val implementationToSpecificationVersion: Map<ArtifactVersion, SpecificationVersion> =
        specificationToImplementationVersion.entries.associateBy({ it.value }) { it.key }

    final override fun implementationForSpecification(specificationVersion: SpecificationVersion): ArtifactVersionCoordinate {
        val defaultImplementation =
            if (specificationVersion <= SpecificationVersion.EE8) javaxCoordinate else jakartaCoordinate
        val version = specificationToImplementationVersion[specificationVersion]!!.toString()
        return defaultImplementation.withVersion(version)
    }

    override fun implementationVersionFor(artifactVersion: ArtifactVersionCoordinate): ArtifactVersion {
        // Deal with the OSGi repackaged bundles versioned 10.x
        if (artifactVersion.module.group == "org.glassfish") {
            return specificationToImplementationVersion[SpecificationVersion.EE7]!!
        }
        return artifactVersion.version
    }

    final override fun specificationForImplementation(version: ArtifactVersion): SpecificationVersion {
        val minorVersion = version.minorVersion
        return implementationToSpecificationVersion[minorVersion]
            ?: SpecificationVersion.EE7
    }

    override fun artifactType(artifactCoordinate: ArtifactCoordinate) =
        ArtifactType.API

    final override val coordinates: List<ArtifactCoordinate>
        get() = javaxCoordinates + jakartaCoordinates
}
