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
import com.netflix.gradle.jakartaee.artifacts.ArtifactType
import com.netflix.gradle.jakartaee.specifications.Specification
import org.gradle.api.artifacts.CacheableRule

@CacheableRule
internal interface Impl : Specification {
    companion object {
        val IMPLEMENTATIONS: List<Impl> = listOf(
            Json(),
            ServerFaces(),
            XmlBind(),
        )
    }

    override val capabilityGroup: String
        get() = "com.netflix.gradle.jakartaee-impl"

    override fun artifactType(artifactCoordinate: ArtifactCoordinate): ArtifactType = ArtifactType.IMPL
}
