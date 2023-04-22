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

internal class Transaction : BasicSpecification(
    "transaction-api",
    JAVAX,
    JAVAX_COORDINATES,
    JAKARTA,
    JAKARTA_COORDINATES,
    SPECIFICATION_TO_TRANSACTION_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish", "javax.transaction") // Repackaged OSGi bundle, appears to be EE 6
        private val JAVAX_1_1 = ArtifactCoordinate("javax.transaction", "transaction-api") // 1.1
        private val JAVAX = ArtifactCoordinate("javax.transaction", "javax.transaction-api") // 1.2 through 1.3
        private val JAKARTA = ArtifactCoordinate("jakarta.transaction", "jakarta.transaction-api") // 1.3.2 and later

        private val SPECIFICATION_TO_TRANSACTION_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.3"),
            SpecificationVersion.EE8 to ArtifactVersion("1.3.3"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("2.0.0"),
        )

        private val JAVAX_COORDINATES = listOf(
            GLASSFISH,
            JAVAX_1_1,
            JAVAX,
        )

        private val JAKARTA_COORDINATES = listOf(
            JAKARTA,
        )
    }
}
