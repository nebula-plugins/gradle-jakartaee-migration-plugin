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

package com.netflix.gradle.jakartaee.artifacts

import org.gradle.api.artifacts.ComponentVariantIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

internal fun ComponentVariantIdentifier.toCoordinate(): ArtifactVersionCoordinate =
    if (id is ModuleComponentIdentifier) {
        val id = id as ModuleComponentIdentifier
        ArtifactCoordinate(id.group, id.module).withVersion(id.version)
    } else throw IllegalArgumentException("Expected an ModuleComponentIdentifier id")

internal val ArtifactVersion.minorVersion: ArtifactVersion
    get() = if (parts.size > 1) ArtifactVersion("${parts[0]}.${parts[1]}") else ArtifactVersion(parts[0])
