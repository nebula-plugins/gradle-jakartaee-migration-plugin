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

internal class RestWebServices : BasicApi(
    "ws.rs-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_RS_VERSION
) {
    companion object {
        private val JAVAX_JSR311 = ArtifactCoordinate("javax.ws.rs", "jsr311-api") // 0.8 through 1.1.1
        private val JAVAX = ArtifactCoordinate("javax.ws.rs", "javax.ws.rs-api") // 2.0 through 2.1.1
        private val JAKARTA = ArtifactCoordinate("jakarta.ws.rs", "jakarta.ws.rs-api") // 2.1.2 and later

        private val SPECIFICATION_TO_RS_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE8 to ArtifactVersion("2.1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.1.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            JAVAX_JSR311,
            JAVAX,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
        )
    }
}
