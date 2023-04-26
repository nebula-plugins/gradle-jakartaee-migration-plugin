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

internal class Servlet : ContainerProvidedApi(
    "servlet-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_SERVLET_VERSION
) {
    companion object {
        private val JAVAX_2 = ArtifactCoordinate("javax.servlet", "servlet-api") // 2.2 through 2.5, also 3.0-alpha-1
        private val JAVAX = ArtifactCoordinate("javax.servlet", "javax.servlet-api") // 3.0.1 through 4.0.1
        private val JAKARTA = ArtifactCoordinate("jakarta.servlet", "jakarta.servlet-api") // 4.0.2 and later

        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.servlet") // Repackaged OSGi bundle, appears to be EE 6
        private val JETTY = ArtifactCoordinate("org.mortbay.jetty", "servlet-api") // 2.5 and 3.0

        private val TOMCAT_5 = ArtifactCoordinate("tomcat", "servlet-api") // 5.0.x though 5.5.x
        private val TOMCAT_6 = ArtifactCoordinate("org.apache.tomcat", "servlet-api") // 6.0.x
        private val TOMCAT = ArtifactCoordinate("org.apache.tomcat", "tomcat-servlet-api") // 7.0.0 and later

        private val SPECIFICATION_TO_SERVLET_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("3.1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("4.0.0"),
            SpecificationVersion.EE9 to ArtifactVersion("5.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("5.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("6.0.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            GLASSFISH,
            JETTY,
            TOMCAT_5,
            TOMCAT_6,
            TOMCAT,
            JAVAX_2,
            JAVAX,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
            TOMCAT_EMBED,
        )
    }
}
