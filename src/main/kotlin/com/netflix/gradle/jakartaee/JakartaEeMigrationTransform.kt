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
