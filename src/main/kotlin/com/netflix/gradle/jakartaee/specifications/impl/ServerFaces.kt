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

package com.netflix.gradle.jakartaee.specifications.impl

import com.netflix.gradle.jakartaee.artifacts.ArtifactCoordinate
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion
import com.netflix.gradle.jakartaee.specifications.SpecificationVersion

internal class ServerFaces : BasicImpl(
    "faces",
    JAVAX_GLASSFISH,
    JAKARTA_GLASSFISH,
    SPECIFICATION_TO_FACES_VERSION
) {
    companion object {
        private val JAVAX_GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.faces") // Bundle. 2.1.2 through 2.2.20
        private val JAKARTA_GLASSFISH = ArtifactCoordinate("org.glassfish", "jakarta.faces") // Bundle. 2.3.9 and later

        private val SPECIFICATION_TO_FACES_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.2.0"),
            SpecificationVersion.EE8 to ArtifactVersion("2.3.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0.0"),
        )
    }
}
