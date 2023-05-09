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
import com.netflix.gradle.jakartaee.specifications.Specification.Companion.IMPLEMENTATIONS
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.attributes.Attribute
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import java.util.concurrent.atomic.AtomicBoolean

public open class JakartaEeMigrationExtension(
    private val project: Project,
) {
    private companion object {
        // Gradle's ArtifactTypeDefinition doesn't have this until 7.3
        private val ARTIFACT_TYPE_ATTRIBUTE = Attribute.of("artifactType", String::class.java)
        private val JAKARTAEE_ATTRIBUTE = Attribute.of("com.netflix.gradle.jakartaee", Boolean::class.javaObjectType)

        // Artifacts that include intentional references to javax packages that should never be transformed
        private val ARTIFACTS_WITH_INTENTIONAL_JAVAX = listOf(
            "org.springframework:spring-context",
            "org.springframework:spring-beans",
            "org.apache.tomcat.embed:tomcat-embed-core",
            "org.apache.groovy:groovy",
            "org.apache.groovy:groovy-all",
        )

        private val PRODUCTION_CONFIGURATION_NAMES = setOf(
            JavaPlugin.API_CONFIGURATION_NAME,
            JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
            JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME,
            JavaPlugin.COMPILE_ONLY_API_CONFIGURATION_NAME,
            JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME,
            JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME,
            JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME,
            JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME
        )

        private val CLASSPATH_NAME_ACCESSORS = setOf<(SourceSet) -> String>(
            { it.apiConfigurationName },
            { it.implementationConfigurationName },
            { it.compileOnlyConfigurationName },
            { it.compileOnlyApiConfigurationName },
            { it.compileClasspathConfigurationName },
            { it.runtimeOnlyConfigurationName },
            { it.runtimeClasspathConfigurationName },
            { it.runtimeElementsConfigurationName }
        )

        private val SPRING_BOOT_CONFIGURATION_NAMES = listOf(
            "developmentOnly",
            "productionRuntimeClasspath",
            "processAotClasspath",
            "processTestAotClasspath")

        private val PROTOPATH_SUFFIX = "ProtoPath"

        private val KOTLIN_DEPENDENCIES_METADATA_SUFFIX = "DependenciesMetadata"

        private val INCLUDED_SUFFIXES = listOf(
            PROTOPATH_SUFFIX,
            KOTLIN_DEPENDENCIES_METADATA_SUFFIX
        )
    }

    private val configuredCapabilities = AtomicBoolean()
    private val registeredTransform = AtomicBoolean()
    private val excludeSpecificationsTransform = AtomicBoolean()
    private val preventTransformOfProductionConfigurations = AtomicBoolean()
    private val included = mutableListOf<ArtifactCoordinate>()
    private val excluded = mutableListOf<ArtifactCoordinate>()

    private val configurations = project.configurations
    private val dependencies = project.dependencies

    init {
        ARTIFACTS_WITH_INTENTIONAL_JAVAX.forEach(::excludeTransform)
    }

    /**
     * Enable automatic migration.
     */
    public fun migrate() {
        val javaExtension = project.extensions.findByType(JavaPluginExtension::class.java)
        check(javaExtension != null) { "The Java plugin extension is not present on this project" }
        javaExtension.sourceSets.configureEach { sourceSet ->
            val configurationNames = CLASSPATH_NAME_ACCESSORS.map { it(sourceSet) }
            project.configurations.configureEach { configuration ->
                if (configurationNames.contains(configuration.name)) {
                    migrate(configuration, true)
                }
            }
        }
        project.configurations.configureEach {configuration ->
            if (SPRING_BOOT_CONFIGURATION_NAMES.contains(configuration.name)) {
                migrate(configuration, true)
            } else if (INCLUDED_SUFFIXES.any { configuration.name.endsWith(it) }) {
                migrate(configuration, true)
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
        migrate(configuration, false)
    }

    private fun migrate(configuration: Configuration, resolveConflicts: Boolean) {
        if (resolveConflicts) {
            resolveCapabilityConflicts(configuration)
        }
        substitute(configuration)
        excludeSpecificationsTransform()
        transform(configuration)
    }

    /**
     * Configure capabilities for the project.
     */
    public fun configureCapabilities() {
        if (configuredCapabilities.compareAndSet(false, true)) {
            IMPLEMENTATIONS.forEach { specification ->
                specification.configureCapabilities(dependencies)
            }
        }
    }

    /**
     * Resolve capability conflicts for a configuration.
     *
     * @param configurationName the configuration to configure
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
        configureCapabilities()
        IMPLEMENTATIONS.forEach { specification ->
            specification.configureCapabilitiesResolution(configuration)
        }
    }

    /**
     * Ensure that at least an EE10 version of all used specifications are available in all configurations.
     */
    public fun substitute() {
        configurations.all { configuration ->
            if (configuration.name != "resolutionRules") {
                substitute(configuration)
            }
        }
    }

    /**
     * Ensure that at least an EE10 version of all used specifications are available in this configuration.
     *
     * @param configurationName the configuration to configure
     */
    public fun substitute(configurationName: String) {
        substitute(configurations.getByName(configurationName))
    }

    /**
     * Ensure that at least an EE10 version of all used specifications are available in this configuration.
     *
     * @param configuration the configuration to configure
     */
    public fun substitute(configuration: Configuration) {
        IMPLEMENTATIONS.forEach { specification ->
            specification.substitute(configuration)
        }
    }

    /**
     * Transform all configurations.
     */
    public fun transform() {
        configurations.all { configuration ->
            if (configuration.name != "resolutionRules") {
                transform(configuration)
            }
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
        if (registeredTransform.compareAndSet(false, true)) {
            registerTransform()
        }
        if (preventTransformOfProductionConfigurations.get() && PRODUCTION_CONFIGURATION_NAMES.contains(configuration.name)) {
            throw IllegalStateException("Use of transforms on production configurations is not allowed (configuration: ${configuration.name})")
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
     * Include an artifact to transformation.
     *
     * @param notation artifact notation in the form group:module
     */
    public fun includeTransform(notation: String) {
        val split = notation.split(":")
        check(split.size == 2) { "Invalid notation, should be in the form group:module" }
        included += ArtifactCoordinate(split[0], split[1])
    }

    /**
     * Exclude all specification artifacts from transformation.
     */
    public fun excludeSpecificationsTransform() {
        if (excludeSpecificationsTransform.compareAndSet(false, true)) {
            val specificationArtifacts = IMPLEMENTATIONS
                .flatMap { specification -> specification.coordinates }
                .distinct()
            excluded += specificationArtifacts
        }
    }

    /**
     * Prevent transforms being configured on well known configurations.
     */
    public fun preventTransformsOfProductionConfigurations() {
        preventTransformOfProductionConfigurations.set(true)
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
                    parameters.setIncludedArtifacts(included)
                    parameters.setExcludedArtifacts(excluded)
                }
            }
        }
    }
}
