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
import org.gradle.work.DisableCachingByDefault
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger


@DisableCachingByDefault(because = "Transform is fast enough not to benefit from caching")
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

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JakartaEeMigrationTransform::class.java)

        init {
            val logger = Logger.getLogger(Migration::class.java.canonicalName)
            logger.useParentHandlers = false
            for (handler in logger.handlers) {
                logger.removeHandler(handler)
            }
            logger.addHandler(TransformHandler(LOGGER))
        }
    }

    /*
     * This is unfortunately a bit of a hack, but you can't use component metadata rules to apply attributes to
     * artifacts. If you add them to the component, they *are* inherited by the artifact, but then the dependency
     * will fail to resolve. Allowing it to resolve with compatibility rules then causes the transform not to run.
     */
    private val excludedPaths by lazy {
        parameters.getExcludedArtifacts().flatMap {
            listOf(
                "/${it.group}/${it.name}/", // Ivy repository layout. Gradle module cache
                "/${it.group.replace(".", "/")}/${it.name}/" // Maven repository layout
            )
        }
    }

    @PathSensitive(PathSensitivity.ABSOLUTE)
    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val inputFile = getInputArtifact().get().asFile
        if (excludedPaths.any { inputFile.path.contains(it) }) {
            LOGGER.debug("Skipping JakartaEE transform for {}", inputFile)
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
            LOGGER.info("Transformed {} to JakartaEE {}", inputFile.name, outputFile.name)
            outputs.file(outputFile)
        } else {
            LOGGER.info("No JakartaEE transformation required for {}", inputFile.name)
            Files.delete(tempFilePath)
            outputs.file(inputFile)
        }
    }

    /**
     * Log handler to push [Migration] logs to info level or below.
     */
    private class TransformHandler(val logger: org.slf4j.Logger) : Handler() {
        override fun publish(record: LogRecord) {
            if (record.level.intValue() < Level.INFO.intValue()) {
                logger.debug(record.message)
            } else {
                logger.info(record.message)
            }
        }

        override fun flush() {
        }

        override fun close() {
        }
    }
}
