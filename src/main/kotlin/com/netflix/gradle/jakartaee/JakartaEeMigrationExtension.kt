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
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet

public open class JakartaEeMigrationExtension(
    private val project: Project,
) {
    private companion object {
        // Gradle's ArtifactTypeDefinition doesn't have this until 7.3
        private val ARTIFACT_TYPE_ATTRIBUTE = Attribute.of("artifactType", String::class.java)
        private val JAKARTAEE_ATTRIBUTE = Attribute.of("com.netflix.gradle.jakartaee", Boolean::class.javaObjectType)

        // Artifacts that include intentional references to javax packages that should never be transformed
        private val ARTIFACTS_WITH_INTENTIONAL_JAVAX = listOf(
            "org.projectlombok:lombok",
            "org.springframework:spring-context",
            "org.springframework:spring-beans",
            "org.apache.tomcat.embed:tomcat-embed-core",
            "org.apache.groovy:groovy",
            "org.apache.groovy:groovy-all",
            "org.codehaus.groovy:groovy",
            "org.codehaus.groovy:groovy-all",
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
            "processTestAotClasspath",
        )

        private val INCLUDED_SUFFIXES = listOf(
            "ProtoPath",
            "DependenciesMetadata",
            "PmdAuxClasspath",
        )
    }

    private val configuredCapabilities: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    private val registeredTransform: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    private val excludeSpecificationsTransform: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    private val transformInMemory: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    private val preventTransformOfProductionConfigurations: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    private val included: ListProperty<ArtifactCoordinate> = project.objects.listProperty(ArtifactCoordinate::class.java).empty()
    private val excluded: ListProperty<ArtifactCoordinate> = project.objects.listProperty(ArtifactCoordinate::class.java).convention(
        ARTIFACTS_WITH_INTENTIONAL_JAVAX.map {
            val split = it.split(":")
            ArtifactCoordinate(split[0], split[1])
        }
    )

    private val configurations = project.configurations
    private val dependencies = project.dependencies

    /**
     * Enable automatic migration.
     */
    public fun migrate() {
        applyToConfigurations({ configuration -> migrate(configuration, true)})
    }

    private fun applyToConfigurations(action: (Configuration) -> Unit) {
        val javaExtension = project.extensions.findByType(JavaPluginExtension::class.java)
        check(javaExtension != null) { "The Java plugin extension is not present on this project" }

        // Build the set of applicable configuration names lazily
        val applicableConfigurationNames = project.provider {
            val sourceSetConfigs = javaExtension.sourceSets.flatMap { sourceSet ->
                CLASSPATH_NAME_ACCESSORS.map { accessor -> accessor(sourceSet) }
            }.toSet()

            sourceSetConfigs + SPRING_BOOT_CONFIGURATION_NAMES
        }

        project.configurations.configureEach { configuration ->
            // Check if this configuration should have the action applied
            val shouldApply = applicableConfigurationNames.get().contains(configuration.name) ||
                              INCLUDED_SUFFIXES.any { configuration.name.endsWith(it) }

            if (shouldApply) {
                action(configuration)
            }
        }
    }

    /**
     * Enable migration for a given configuration by name.
     *
     * @param configurationName the name of the configuration to be migrated
     */
    public fun migrate(configurationName: String) {
        configurations.named(configurationName).configure { configuration ->
            migrate(configuration)
        }
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
        if (!configuredCapabilities.get()) {
            configuredCapabilities.set(true)
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
        configurations.named(configurationName).configure { configuration ->
            resolveCapabilityConflicts(configuration)
        }
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
        applyToConfigurations(::substitute)
    }

    /**
     * Ensure that at least an EE10 version of all used specifications are available in this configuration.
     *
     * @param configurationName the configuration to configure
     */
    public fun substitute(configurationName: String) {
        configurations.named(configurationName).configure { configuration ->
            substitute(configuration)
        }
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
        applyToConfigurations(::transform)
    }

    /**
     * Transform artifacts for the given configuration name.
     *
     * @param configurationName the name of the configuration to transform
     */
    public fun transform(configurationName: String) {
        configurations.named(configurationName).configure { configuration ->
            transform(configuration)
        }
    }

    /**
     * Transform artifacts for the given configuration name.
     *
     * @param configuration the configuration to transform
     */
    public fun transform(configuration: Configuration) {
        if (!registeredTransform.get()) {
            registeredTransform.set(true)
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
        excluded.add(ArtifactCoordinate(split[0], split[1]))
    }

    /**
     * Include an artifact to transformation.
     *
     * @param notation artifact notation in the form group:module
     */
    public fun includeTransform(notation: String) {
        val split = notation.split(":")
        check(split.size == 2) { "Invalid notation, should be in the form group:module" }
        included.add(ArtifactCoordinate(split[0], split[1]))
    }

    /**
     * Exclude all specification artifacts from transformation.
     */
    public fun excludeSpecificationsTransform() {
        if (!excludeSpecificationsTransform.get()) {
            excludeSpecificationsTransform.set(true)
            val specificationArtifacts = IMPLEMENTATIONS
                .flatMap { specification -> specification.coordinates }
                .distinct()
            excluded.addAll(specificationArtifacts)
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
            listOf(ArtifactTypeDefinition.JAR_TYPE, "test-jar").forEach { artifactType ->
                registerTransform(JakartaEeMigrationTransform::class.java) {
                    with(it) {
                        from.attribute(JAKARTAEE_ATTRIBUTE, false)
                                .attribute(ARTIFACT_TYPE_ATTRIBUTE, artifactType)
                        to.attribute(JAKARTAEE_ATTRIBUTE, true)
                                .attribute(ARTIFACT_TYPE_ATTRIBUTE, artifactType)
                        parameters.includedArtifacts.set(included)
                        parameters.excludedArtifacts.set(excluded)
                        parameters.transformInMemory.set(transformInMemory)
                    }
                }
            }
        }
    }

    /**
     * Enables inmemory zip processing for migration tool.
     */
    public fun transformInMemory() {
        transformInMemory.set(true)
    }
}
