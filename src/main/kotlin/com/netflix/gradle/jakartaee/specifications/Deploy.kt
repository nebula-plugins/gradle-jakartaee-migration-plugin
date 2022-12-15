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

internal class Deploy : BasicSpecification(
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_DEPLOY_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.enterprise.deploy") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX = ArtifactCoordinate("javax.enterprise.deploy", "javax.enterprise.deploy-api") // 1.6, 1.7
        private val JAKARTA = ArtifactCoordinate("jakarta.enterprise.deploy", "jakarta.enterprise.deploy-api") // 1.7.1 and later

        private val SPECIFICATION_TO_DEPLOY_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.6"),
            SpecificationVersion.EE8 to ArtifactVersion("1.7"),
            // Optional APIs removed from the BOM for 9.0.0 and later, use last released version
            SpecificationVersion.EE9 to ArtifactVersion("1.7"),
            SpecificationVersion.EE9_1 to ArtifactVersion("1.7"),
            SpecificationVersion.EE10 to ArtifactVersion("1.7"),
        )
    }

    override val name: String
        get() = "enterprise.deploy-api"

    override val coordinates: List<ArtifactCoordinate>
        get() = listOf(
            GLASSFISH,
            JAVAX,
            JAKARTA,
        )
}
