/*
 * Copyright 2023 Netflix, Inc.
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

package com.netflix.gradle.jakartaee.specifications.api

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion
import com.netflix.gradle.jakartaee.specifications.SpecificationVersion

internal class Management : BasicApi(
    "management.j2ee-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_MANAGEMENT_VERSION
) {
    companion object {
        private val GLASSFISH =
            ArtifactCoordinate("org.glassfish", "javax.management.j2ee") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX =
            ArtifactCoordinate("javax.management.j2ee", "javax.management.j2ee-api") // 1.1 through 1.1.2
        private val JAKARTA =
            ArtifactCoordinate("jakarta.management.j2ee", "jakarta.management.j2ee-api") // 1.1.3 and later

        private val SPECIFICATION_TO_MANAGEMENT_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.1.0"),
            // Optional APIs removed from the BOM for 9.0.0 and later, use last released version
            SpecificationVersion.EE9 to ArtifactVersion("1.1.4"),
            SpecificationVersion.EE9_1 to ArtifactVersion("1.1.4"),
            SpecificationVersion.EE10 to ArtifactVersion("1.1.4"),
        )

        private val JAVAX_COORDINATES = listOf(
            GLASSFISH,
            JAVAX,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
        )
    }
}
