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

internal class JavaBeans : BasicApi(
    "ejb-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_EJB_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.ejb") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.ejb", "ejb") // 2.0 through 2.1
        private val JAVAX_3_0 = ArtifactCoordinate("javax.ejb", "ejb-api") // 3.0
        private val JAVAX_3_2 = ArtifactCoordinate("javax.ejb", "javax.ejb-api") // 3.2 through 3.2.2
        private val JAKARTA = ArtifactCoordinate("jakarta.ejb", "jakarta.ejb-api") // 3.2.3 and later

        private val SPECIFICATION_TO_EJB_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("3.2.0"),
            SpecificationVersion.EE8 to ArtifactVersion("3.2.0"),
            SpecificationVersion.EE9 to ArtifactVersion("4.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("4.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            GLASSFISH,
            JAVAX,
            JAVAX_3_0,
            JAVAX_3_2,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
        )
    }
}
