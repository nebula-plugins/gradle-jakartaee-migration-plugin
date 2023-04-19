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

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactType
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class ServerFaces : BasicSpecification(
    "faces-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_FACES_VERSION
) {
    companion object {
        private val JAVAX_LEGACY = ArtifactCoordinate("javax.faces", "jsf-api") // 1.0 through 2.1
        private val JAVAX = ArtifactCoordinate("javax.faces", "javax.faces-api") // 2.1 through 2.3
        private val JAVAX_GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.faces") // Bundle. 2.1.2 through 2.2.20
        private val JAKARTA = ArtifactCoordinate("jakarta.faces", "jakarta.faces-api") // 2.3.1 and later
        private val JAKARTA_GLASSFISH = ArtifactCoordinate("org.glassfish", "jakarta.faces") // Bundle. 2.3.9 and later

        private val SPECIFICATION_TO_FACES_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.2"),
            SpecificationVersion.EE8 to ArtifactVersion("2.3"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            JAVAX_LEGACY,
            JAVAX,
            JAVAX_GLASSFISH,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
            JAKARTA_GLASSFISH,
        )
    }
}
