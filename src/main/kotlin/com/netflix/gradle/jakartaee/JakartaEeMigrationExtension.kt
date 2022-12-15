package com.netflix.gradle.jakartaee

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.specifications.Specification
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.attributes.Attribute

public open class JakartaEeMigrationExtension(
    private val configurations: ConfigurationContainer,
    private val dependencies: DependencyHandler
) {
    private companion object {
        // Gradle's ArtifactTypeDefinition doesn't have this until 7.3
        private val ARTIFACT_TYPE_ATTRIBUTE = Attribute.of("artifactType", String::class.java)
        private val JAKARTAEE_ATTRIBUTE = Attribute.of("com.netflix.gradle.jakartaee", Boolean::class.javaObjectType)
    }

    private val excluded = mutableListOf<ArtifactCoordinate>()

    /**
     * Enable migration for all resolvable (i.e. isCanBeResolved == true) configurations.
     */
    public fun migrateResolvableConfigurations() {
        configurations.all { configuration ->
            if (configuration.isCanBeResolved) {
                migrateConfiguration(configuration)
            }
        }
    }

    /**
     * Enable migration for a given configuration by name.
     *
     * @param configurationName the name of the configuration to be migrated
     */
    public fun migrateConfigurationNamed(configurationName: String) {
        migrateConfiguration(configurations.getByName(configurationName))
    }

    /**
     * Enable migration for a given configuration.
     *
     * @param configuration the configuration to be migrated
     */
    public fun migrateConfiguration(configuration: Configuration) {
        check(configuration.isCanBeResolved) { "Configuration ${configuration.name} cannot be resolved" }
        configuration.attributes.attribute(JAKARTAEE_ATTRIBUTE, true)
        Specification.SPECIFICATIONS.forEach { specification ->
            specification.configureCapabilitiesResolution(configuration)
        }
    }

    /**
     * Exclude an artifact from migration.
     *
     * @param notation artifact notation in the form group:module
     */
    public fun exclude(notation: String) {
        val split = notation.split(":")
        check(split.size == 2) { "Invalid notation, should be in the form group:module" }
        excluded += ArtifactCoordinate(split[0], split[1])
    }

    /**
     * Exclude all specification artifacts from the migration.
     */
    public fun excludeSpecificationArtifacts() {
        val specificationArtifacts = Specification.SPECIFICATIONS
            .flatMap { specification -> specification.coordinates }
            .distinct()
        excluded += specificationArtifacts
    }

    internal fun registerCapabilities() {
        Specification.SPECIFICATIONS.forEach { specification ->
            specification.configureCapabilities(dependencies)
        }
    }

    internal fun registerTransform() {
        with(dependencies) {
            attributesSchema {
                it.attribute(JAKARTAEE_ATTRIBUTE)
            }
            artifactTypes.named(ArtifactTypeDefinition.JAR_TYPE) {
                it.attributes.attribute(JAKARTAEE_ATTRIBUTE, false)
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
