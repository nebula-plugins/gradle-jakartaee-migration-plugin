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

internal class ExpressionLanguage : ContainerProvidedApi(
    "el-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_EL_VERSION
) {
    companion object {
        private val JAVAX_2 = ArtifactCoordinate("javax.el", "el-api") // 1.0 through 2.2
        private val JAVAX = ArtifactCoordinate("javax.el", "javax.el-api") // 2.2.1 through 3.0.0 (also 1.1.2 patch release)
        private val JAKARTA = ArtifactCoordinate("jakarta.el", "jakarta.el-api") // 3.0.2 and later

        private val TOMCAT_6 = ArtifactCoordinate("org.apache.tomcat", "el-api") // 6.0.x
        private val TOMCAT = ArtifactCoordinate("org.apache.tomcat", "tomcat-el-api") // 7.0.0 and later
        private val TOMCAT_EMBED_EL = ArtifactCoordinate("org.apache.tomcat.embed", "tomcat-embed-el") // 7.0.0 and later, includes implementation

        private val SPECIFICATION_TO_EL_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE8 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE9 to ArtifactVersion("4.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("4.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("5.0.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            TOMCAT_6,
            TOMCAT,
            JAVAX_2,
            JAVAX,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
            TOMCAT_EMBED_EL
        )
    }
}
