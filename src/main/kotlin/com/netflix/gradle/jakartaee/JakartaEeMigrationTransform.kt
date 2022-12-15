package com.netflix.gradle.jakartaee

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import org.apache.tomcat.jakartaee.EESpecProfiles
import org.apache.tomcat.jakartaee.Migration
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.slf4j.LoggerFactory
import java.nio.file.Files

internal abstract class JakartaEeMigrationTransform : TransformAction<JakartaEeMigrationTransform.Parameters> {
    interface Parameters : TransformParameters {
        /*
         * Transforms cannot be applied conditionally, only to all artifacts of a given type, and transforms themselves
         * aren't dependency resolution specific, so we can't get access to the details of artifact, so we have to
         * rely on the filename for excludes.
         */
        @Input
        fun getExcludedArtifacts(): List<ArtifactCoordinate>
        fun setExcludedArtifacts(excludedArtifact: List<ArtifactCoordinate>)
    }

    private val logger = LoggerFactory.getLogger(JakartaEeMigrationTransform::class.java)
    private val excludedCachePaths by lazy {
        parameters.getExcludedArtifacts().map {
            "/${it.group}/${it.name}/"
        }
    }

    @PathSensitive(PathSensitivity.ABSOLUTE)
    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val inputFile = getInputArtifact().get().asFile
        if (excludedCachePaths.any { inputFile.path.contains(it) }) {
            logger.debug("Skipping JakartaEE transform for {}", inputFile)
            outputs.file(inputFile)
            return
        }

        val migration = Migration()
        migration.setSource(inputFile)
        val tempFilePath = Files.createTempFile("jakartaee", "transform")
        migration.setDestination(tempFilePath.toFile())
        migration.eeSpecProfile = EESpecProfiles.EE
        migration.setEnableDefaultExcludes(false)

        migration.execute()
        if (migration.hasConverted()) {
            val outputFile = outputs.file("${inputFile.nameWithoutExtension}-jakartaee.jar")
            Files.move(tempFilePath, outputFile.toPath())
            logger.info("Transformed {} to JakartaEE {}", inputFile.name, outputFile.name)
            outputs.file(outputFile)
        } else {
            logger.info("No JakartaEE transformation required for {}", inputFile.name)
            Files.delete(tempFilePath)
            outputs.file(inputFile)
        }
    }
}
