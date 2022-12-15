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
import org.gradle.api.Project
import org.gradle.api.artifacts.CacheableRule
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.dsl.DependencyHandler

@CacheableRule
internal interface Specification : ComponentMetadataRule {
    companion object {
        const val CAPABILITY_GROUP = "com.netflix.gradle.jakartaee"

        val SPECIFICATIONS: List<Specification> = listOf(
            Activation(),
            Annotation(),
            Authentication(),
            Authorization(),
            Batch(),
            Cdi(),
            Concurrent(),
            Deploy(),
            ExpressionLanguage(),
            Inject(),
            Interceptor(),
            JavaBeans(),
            Json(),
            JsonBind(),
            Management(),
            Mail(),
            MessageService(),
            Persistence(),
            Resource(),
            RestWebServices(),
            Security(),
            ServerFaces(),
            ServerPages(),
            Servlet(),
            StandardTagLibrary(),
            Transaction(),
            Validation(),
            WebServicesMetadata(),
            WebSocket(),
            WebSocketClient(),
            XmlBind(),
            XmlRegistry(),
            XmlRpc(),
            XmlSoap(),
            XmlWebServices()
        )
    }

    /**
     * The name of the specification, for example, servlet-api.
     */
    val name: String

    /**
     * Coordinates of the known implementations of this specification in order of preference.
     */
    val coordinates: List<ArtifactCoordinate>

    fun implementationsForSpecification(specificationVersion: SpecificationVersion): List<ArtifactVersionCoordinate>

    fun implementationVersionFor(artifactVersion: ArtifactVersionCoordinate): ArtifactVersion

    fun artifactType(artifactCoordinate: ArtifactCoordinate): ArtifactType = ArtifactType.API

    fun configureCapabilities(dependencies: DependencyHandler) {
        val components = dependencies.components
        coordinates.forEach { coordinate ->
            components.withModule(coordinate.notation, this::class.java)
        }
    }

    override fun execute(context: ComponentMetadataContext) {
        context.details.allVariants { metadata ->
            metadata.withCapabilities {
                it.addCapability(CAPABILITY_GROUP, name, context.details.id.version)
            }
        }
    }

    fun configureCapabilitiesResolution(configuration: Configuration) {
        val coordinateToOrdinal = coordinates
            .mapIndexed { index: Int, coordinate: ArtifactCoordinate -> coordinate to index }
            .toMap()

        configuration.resolutionStrategy
            .capabilitiesResolution
            .withCapability("${CAPABILITY_GROUP}:${name}") { details ->
                // selectHighestVersion but using the specification version provided by the artifact
                details.because("Provides the highest EE specification version")
                val candidate = details.candidates
                    .filter { it.id is ModuleComponentIdentifier }
                    .sortedByDescending {
                        val coordinate = (it.id as ModuleComponentIdentifier).toArtifactCoordinate()
                        coordinateToOrdinal[coordinate.module]
                    }.maxByOrNull {
                        val coordinate = (it.id as ModuleComponentIdentifier).toArtifactCoordinate()
                        if (artifactType(coordinate.module) == ArtifactType.BUNDLE) {
                            details.because("Provides a bundled EE API and implementation")
                            // Embedded implementations win regardless of specification/package provided
                            ArtifactVersion(Integer.MAX_VALUE.toString())
                        } else {
                            implementationVersionFor(coordinate)
                        }
                    } ?: return@withCapability
                details.select(candidate)
            }
    }
}
