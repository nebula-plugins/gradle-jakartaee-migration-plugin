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

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion
import com.netflix.gradle.jakartaee.specifications.BasicSpecification
import com.netflix.gradle.jakartaee.specifications.SpecificationVersion

internal abstract class BasicApi(
    name: String,
    javaxCoordinate: ArtifactCoordinate,
    javaxCoordinates: List<ArtifactCoordinate>,
    jakartaCoordinate: ArtifactCoordinate,
    jakartaCoordinates: List<ArtifactCoordinate>,
    specificationToImplementationVersion: Map<SpecificationVersion, ArtifactVersion>
) : BasicSpecification(
    name,
    javaxCoordinate,
    javaxCoordinates,
    jakartaCoordinate,
    jakartaCoordinates,
    specificationToImplementationVersion,
), Api {
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
}
