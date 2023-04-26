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

package com.netflix.gradle.jakartaee.specifications

import com.netflix.gradle.jakartaee.artifacts.*
import com.netflix.gradle.jakartaee.specifications.api.Api
import com.netflix.gradle.jakartaee.specifications.impl.Impl
import org.gradle.api.artifacts.CacheableRule
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.dsl.DependencyHandler

@CacheableRule
internal interface Specification : ComponentMetadataRule {

    companion object {
        val IMPLEMENTATIONS: List<Specification> = Api.IMPLEMENTATIONS + Impl.IMPLEMENTATIONS
    }

    /**
     * The name of the specification, for example, servlet-api.
     */
    val name: String

    /**
     * Coordinates of the known implementations of this specification in order of preference.
     */
    val coordinates: List<ArtifactCoordinate>

    /**
     * Coordinates of known javax packages.
     */
    val javaxCoordinates: List<ArtifactCoordinate>

    /**
     * Coordinates of known jakarta packages.
     */
    val jakartaCoordinates: List<ArtifactCoordinate>

    val capabilityGroup: String

    fun artifactType(artifactCoordinate: ArtifactCoordinate): ArtifactType

    fun implementationVersionFor(artifactVersion: ArtifactVersionCoordinate): ArtifactVersion

    fun implementationForSpecification(specificationVersion: SpecificationVersion): ArtifactVersionCoordinate

    fun configureCapabilities(dependencies: DependencyHandler) {
        val components = dependencies.components
        coordinates.forEach { coordinate ->
            components.withModule(coordinate.notation, this::class.java)
        }
    }

    override fun execute(context: ComponentMetadataContext) {
        context.details.allVariants { metadata ->
            metadata.withCapabilities {
                it.addCapability(capabilityGroup, name, context.details.id.version)
            }
        }
    }

    fun configureCapabilitiesResolution(configuration: Configuration) {
        val coordinateToOrdinal = coordinates
            .mapIndexed { index: Int, coordinate: ArtifactCoordinate -> coordinate to index }
            .toMap()
        val capability = "${capabilityGroup}:${name}"
        configuration.resolutionStrategy
            .capabilitiesResolution
            .withCapability(capability) { details ->
                // selectHighestVersion but using the specification version provided by the artifact
                details.because("Provides the highest EE specification version")
                val candidate = details.candidates
                    .filter { it.id is ModuleComponentIdentifier }
                    .sortedByDescending {
                        val coordinate = it.toCoordinate()
                        coordinateToOrdinal[coordinate.module]
                    }.maxBy {
                        val coordinate = it.toCoordinate()
                        if (artifactType(coordinate.module) == ArtifactType.EMBED) {
                            details.because("Provides an embedded EE API and implementation")
                            // Embedded implementations win regardless of specification/package provided
                            ArtifactVersion(Integer.MAX_VALUE.toString())
                        } else {
                            implementationVersionFor(coordinate)
                        }
                    }

                details.select(candidate)
            }
    }

    fun substitute(configuration: Configuration) {
        configuration.resolutionStrategy.dependencySubstitution { substitution ->
            val jakartaImplementation = implementationForSpecification(SpecificationVersion.EE9)
            val to = substitution.module(jakartaImplementation.notation)
            javaxCoordinates.forEach { coordinate ->
                val from = substitution.module(coordinate.notation)
                substitution.substitute(from).using(to).because("At least Jakarta EE 9 is required")
            }
        }
    }

}
