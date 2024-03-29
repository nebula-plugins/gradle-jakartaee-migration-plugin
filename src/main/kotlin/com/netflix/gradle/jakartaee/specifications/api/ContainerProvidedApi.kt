/*
 * Copyright 2023 Netflix, Inc.
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

package com.netflix.gradle.jakartaee.specifications.api

import com.netflix.gradle.jakartaee.artifacts.*
import com.netflix.gradle.jakartaee.specifications.SpecificationVersion

internal abstract class ContainerProvidedApi(
    name: String,
    javaxCoordinate: ArtifactCoordinate,
    javaxCoordinates: List<ArtifactCoordinate>,
    jakartaCoordinate: ArtifactCoordinate,
    jakartaCoordinates: List<ArtifactCoordinate>,
    private val specificationToImplementationVersion: Map<SpecificationVersion, ArtifactVersion>
) : BasicApi(
    name,
    javaxCoordinate,
    javaxCoordinates,
    jakartaCoordinate,
    jakartaCoordinates,
    specificationToImplementationVersion
), Api {
    companion object {
        internal val TOMCAT_TO_EE_VERSION = mapOf(
            ArtifactVersion("8.0") to SpecificationVersion.EE7,
            ArtifactVersion("8.5") to SpecificationVersion.EE7,
            ArtifactVersion("9.0") to SpecificationVersion.EE8,
            ArtifactVersion("10.0") to SpecificationVersion.EE9,
            ArtifactVersion("10.1") to SpecificationVersion.EE10
        )

        internal val TOMCAT_EMBED =
            ArtifactCoordinate("org.apache.tomcat.embed", "tomcat-embed-core") // 7.0.0 and later
    }

    override fun implementationVersionFor(artifactVersion: ArtifactVersionCoordinate): ArtifactVersion {
        var implementationVersion: ArtifactVersion = super.implementationVersionFor(artifactVersion)
        val group = artifactVersion.module.group
        if (group.startsWith("tomcat") || group.startsWith("org.apache.tomcat")) {
            val specificationVersion = TOMCAT_TO_EE_VERSION[implementationVersion.minorVersion]
                ?: SpecificationVersion.EE7
            implementationVersion = specificationToImplementationVersion[specificationVersion]!!
        }
        return implementationVersion
    }

    override fun artifactType(artifactCoordinate: ArtifactCoordinate): ArtifactType {
        return if (artifactCoordinate.group == "org.apache.tomcat.embed") {
            ArtifactType.EMBED
        } else super<Api>.artifactType(artifactCoordinate)
    }
}
