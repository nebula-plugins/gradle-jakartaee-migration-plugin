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
