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

package com.netflix.gradle.jakartaee

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.specifications.Specification
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.attributes.Attribute
import java.util.concurrent.atomic.AtomicBoolean

public open class JakartaEeMigrationExtension(
    private val configurations: ConfigurationContainer,
    private val dependencies: DependencyHandler
) {
    private companion object {
        // Gradle's ArtifactTypeDefinition doesn't have this until 7.3
        private val ARTIFACT_TYPE_ATTRIBUTE = Attribute.of("artifactType", String::class.java)
        private val JAKARTAEE_ATTRIBUTE = Attribute.of("com.netflix.gradle.jakartaee", Boolean::class.javaObjectType)
    }

    private val configuredCapabilities = AtomicBoolean()
    private val registeredTransform = AtomicBoolean()
    private val excluded = mutableListOf<ArtifactCoordinate>()

    /**
     * Enable migration for all resolvable (i.e. isCanBeResolved == true) configurations.
     */
    public fun migrate() {
        configurations.all { configuration ->
            if (configuration.isCanBeResolved) {
                migrate(configuration)
            }
        }
    }

    /**
     * Enable migration for a given configuration by name.
     *
     * @param configurationName the name of the configuration to be migrated
     */
    public fun migrate(configurationName: String) {
        migrate(configurations.getByName(configurationName))
    }

    /**
     * Enable migration for a given configuration.
     *
     * @param configuration the configuration to be migrated
     */
    public fun migrate(configuration: Configuration) {
        resolveCapabilityConflicts(configuration)
        transform(configuration)
    }

    /**
     * Configure capabilities for the project.
     */
    public fun configureCapabilities() {
        if (configuredCapabilities.compareAndSet(false, true)) {
            Specification.SPECIFICATIONS.forEach { specification ->
                specification.configureCapabilities(dependencies)
            }
        }
    }

    /**
     * Resolve capability conflicts for a configuration.
     *
     * @param configuration the configuration to configure
     */
    public fun resolveCapabilityConflicts(configurationName: String) {
        resolveCapabilityConflicts(configurations.getByName(configurationName))
    }

    /**
     * Enable capability conflict resolution for a configuration.
     *
     * @param configuration the configuration to configure
     */
    public fun resolveCapabilityConflicts(configuration: Configuration) {
        check(configuration.isCanBeResolved) { "Configuration ${configuration.name} cannot be resolved" }
        configureCapabilities()
        Specification.SPECIFICATIONS.forEach { specification ->
            specification.configureCapabilitiesResolution(configuration)
        }
    }

    /**
     * Transform artifacts for the given configuration name.
     *
     * @param configurationName the name of the configuration to transform
     */
    public fun transform(configurationName: String) {
        transform(configurations.getByName(configurationName))
    }

    /**
     * Transform artifacts for the given configuration name.
     *
     * @param configuration the configuration to transform
     */
    public fun transform(configuration: Configuration) {
        check(configuration.isCanBeResolved) { "Configuration ${configuration.name} cannot be resolved" }
        if (registeredTransform.compareAndSet(false, true)) {
            registerTransform()
        }
        configuration.attributes.attribute(JAKARTAEE_ATTRIBUTE, true)
    }

    /**
     * Exclude an artifact from transformation.
     *
     * @param notation artifact notation in the form group:module
     */
    public fun excludeTransform(notation: String) {
        val split = notation.split(":")
        check(split.size == 2) { "Invalid notation, should be in the form group:module" }
        excluded += ArtifactCoordinate(split[0], split[1])
    }

    /**
     * Exclude all specification artifacts from transformation.
     */
    public fun excludeSpecificationsTransform() {
        val specificationArtifacts = Specification.SPECIFICATIONS
            .flatMap { specification -> specification.coordinates }
            .distinct()
        excluded += specificationArtifacts
    }

    private fun registerTransform() {
        with(dependencies) {
            attributesSchema {
                it.attribute(JAKARTAEE_ATTRIBUTE)
            }
            artifactTypes.configureEach {
                if (it.name == ArtifactTypeDefinition.JAR_TYPE) {
                    it.attributes.attribute(JAKARTAEE_ATTRIBUTE, false)
                }
            }
            registerTransform(JakartaEeMigrationTransform::class.java) {
                with(it) {
                    from.attribute(JAKARTAEE_ATTRIBUTE, false)
                        .attribute(ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.JAR_TYPE)
                    to.attribute(JAKARTAEE_ATTRIBUTE, true)
                        .attribute(ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.JAR_TYPE)
                    parameters.setExcludedArtifacts(excluded)
                }
            }
        }
    }

}
