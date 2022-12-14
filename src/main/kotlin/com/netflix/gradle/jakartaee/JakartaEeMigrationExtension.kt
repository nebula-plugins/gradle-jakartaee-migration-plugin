package com.netflix.gradle.jakartaee

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.specifications.Specification
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.attributes.Attribute

open class JakartaEeMigrationExtension(
    private val configurations: ConfigurationContainer,
    private val dependencies: DependencyHandler
) {
    companion object {
        // Gradle's ArtifactTypeDefinition doesn't have this until 7.3
        private val ARTIFACT_TYPE_ATTRIBUTE = Attribute.of("artifactType", String::class.java)
        private val JAKARTAEE_ATTRIBUTE = Attribute.of("com.netflix.gradle.jakartaee", Boolean::class.javaObjectType)

        /**
         * Artifacts that intentionally reference javax packages and/or should be upgraded for Jakarta support should
         * not be transformed.
         */
        private val INTENTIONAL_JAVAX_ARTIFACTS = listOf(
            ArtifactCoordinate("org.springframework", "spring-context"),
            ArtifactCoordinate("org.springframework", "spring-beans"),
        )
    }

    private val excluded = mutableListOf<ArtifactCoordinate>()

    init {
        val specificationArtifacts = Specification.SPECIFICATIONS
            .flatMap { specification -> specification.coordinates }
            .distinct()
        excluded += INTENTIONAL_JAVAX_ARTIFACTS
        excluded += specificationArtifacts
    }

    fun migrateResolvableConfigurations() {
        configurations.all { configuration ->
            if (configuration.isCanBeResolved) {
                migrateConfiguration(configuration)
            }
        }
    }

    fun migrateConfigurationNamed(configurationName: String) {
        migrateConfiguration(configurations.getByName(configurationName))
    }

    fun migrateConfiguration(configuration: Configuration) {
        check(configuration.isCanBeResolved) { "Configuration ${configuration.name} cannot be resolved" }
        configuration.attributes.attribute(JAKARTAEE_ATTRIBUTE, true)
        Specification.SPECIFICATIONS.forEach { specification ->
            specification.configureCapabilitiesResolution(configuration)
        }
    }

    fun exclude(notation: String) {
        val split = notation.split(":")
        check(split.size == 2) { "Invalid notation, should be in the form group:module" }
        excluded += ArtifactCoordinate(split[0], split[1])
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
