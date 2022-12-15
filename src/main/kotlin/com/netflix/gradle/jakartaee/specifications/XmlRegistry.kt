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

internal class XmlRegistry : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_XML_REGISTRY_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.xml.registry", "javax.xml.registry-api") // 1.0.4 through 1.0.8
        private val JAKARTA = ArtifactCoordinate("jakarta.xml.registry", "jakarta.xml.registry-api") // 1.0.9 and later

        private val SPECIFICATION_TO_XML_REGISTRY_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.0"),
            // Optional APIs removed from the BOM for 9.0.0 and later, use last released version
            SpecificationVersion.EE9 to ArtifactVersion("1.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("1.0"),
            SpecificationVersion.EE10 to ArtifactVersion("1.0"),
        )
    }

    override val name: String
        get() = "xml.registry-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            JAVAX,
            JAKARTA,
        )
}
