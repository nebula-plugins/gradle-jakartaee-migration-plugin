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
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion

internal class XmlSoap : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_CDI_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.xml.soap") // Repackaged OSGi bundle, appears to be EE 6
        private val SAAJ_1_X = ArtifactCoordinate("javax.xml", "saaj-api") // 1.2. 1.3 relocates to javax.xml.soap
        private val SAAJ = ArtifactCoordinate("javax.xml.soap", "saaj-api") // 1.3 through 1.3.5
        private val JAVAX = ArtifactCoordinate("javax.xml.soap", "javax.xml.soap-api") // 1.3.5 through 1.4.0
        private val JAKARTA = ArtifactCoordinate("jakarta.xml.soap", "jakarta.xml.soap-api") // 1.4.1 and later

        private val SPECIFICATION_TO_CDI_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.3"),
            SpecificationVersion.EE8 to ArtifactVersion("1.3"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0"),
        )
    }

    override val name: String
        get() = "xml.soap-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            SAAJ_1_X,
            SAAJ,
            JAVAX,
            JAKARTA,
        )
}
