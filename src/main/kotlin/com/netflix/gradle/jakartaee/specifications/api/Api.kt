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
import com.netflix.gradle.jakartaee.artifacts.ArtifactType
import com.netflix.gradle.jakartaee.specifications.Specification

internal interface Api : Specification {
    companion object {
        val IMPLEMENTATIONS: List<Api> = listOf(
            Activation(),
            Annotation(),
            Authentication(),
            Authorization(),
            Batch(),
            Cdi(),
            Concurrent(),
            Deploy(),
            ExpressionLanguage(),
            Inject(),
            Interceptor(),
            JavaBeans(),
            Json(),
            JsonBind(),
            Management(),
            Mail(),
            MessageService(),
            Persistence(),
            Resource(),
            RestWebServices(),
            Security(),
            ServerFaces(),
            ServerPages(),
            Servlet(),
            StandardTagLibrary(),
            Transaction(),
            Validation(),
            WebServicesMetadata(),
            WebSocket(),
            WebSocketClient(),
            XmlBind(),
            XmlRegistry(),
            XmlRpc(),
            XmlSoap(),
            XmlWebServices()
        )
    }

    override val capabilityGroup: String
        get() = "com.netflix.gradle.jakartaee"

    override fun artifactType(artifactCoordinate: ArtifactCoordinate) = ArtifactType.API
}
