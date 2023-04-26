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

internal class StandardTagLibrary : BasicApi(
    "jsp.jstl-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_STL_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.servlet.jsp.jstl") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_LEGACY = ArtifactCoordinate("jstl", "jstl") // 1.0.3 through 1.2
        private val JAVAX_1_2 = ArtifactCoordinate("javax.servlet.jsp.jstl", "jstl") // 1.2
        private val JAVAX = ArtifactCoordinate("javax.servlet.jsp.jstl", "javax.servlet.jsp.jstl-api") // 1.2.1, 1.2.2
        private val JAKARTA = ArtifactCoordinate("jakarta.servlet.jsp.jstl", "jakarta.servlet.jsp.jstl-api") // 1.2.3 and later

        private val SPECIFICATION_TO_STL_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.2.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.2.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            GLASSFISH,
            JAVAX_LEGACY,
            JAVAX_1_2,
            JAVAX,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
        )
    }
}
