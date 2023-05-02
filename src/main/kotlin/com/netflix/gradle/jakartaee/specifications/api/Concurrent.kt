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

internal class Concurrent : BasicApi(
    "enterprise.concurrent-api",
    JAVAX,
    JAKARTA,
    SPECIFICATION_TO_CONCURRENT_VERSION
) {
    companion object {
        private val JAVAX = ArtifactCoordinate("javax.enterprise.concurrent", "javax.enterprise.concurrent-api") // 1.0, 1.1
        private val JAKARTA = ArtifactCoordinate("jakarta.enterprise.concurrent", "jakarta.enterprise.concurrent-api") // 1.1.1 and later

        private val SPECIFICATION_TO_CONCURRENT_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("1.0.0"),
            SpecificationVersion.EE8 to ArtifactVersion("1.1.0"),
            SpecificationVersion.EE9 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("2.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("3.0.2"),
        )
    }
}
