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
import com.netflix.gradle.jakartaee.artifacts.ArtifactVersion
import com.netflix.gradle.jakartaee.specifications.SpecificationVersion
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySubstitution
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.internal.artifacts.ivyservice.dependencysubstitution.DefaultDependencySubstitutions

/**
 * JAXB is a case where artifact coordinates have not changed and the EE4J project continues to release at both
 * coordinates. There's no coordinate 'javax' to 'jakarta' divide, only version, so we do some slightly unusual things
 * here compared to all other artifacts.
 *
 * For background see:
 *
 * https://stackoverflow.com/questions/71095913/what-is-the-difference-between-jaxb-impl-and-jaxb-runtime/72151763
 */
internal class XmlBind : BasicImpl(
    "xml.bind",
    GLASSFISH,
    SUN,
    SPECIFICATION_TO_XML_BIND_VERSION
) {
    companion object {
        private val GLASSFISH = ArtifactCoordinate("org.glassfish.jaxb", "jaxb-runtime")
        private val SUN = ArtifactCoordinate("com.sun.xml.bind", "jaxb-impl")

        private val SPECIFICATION_TO_XML_BIND_VERSION = mapOf(
            SpecificationVersion.EE7 to ArtifactVersion("2.2.0"),
            SpecificationVersion.EE8 to ArtifactVersion("2.3.0"),
            SpecificationVersion.EE9 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE9_1 to ArtifactVersion("3.0.0"),
            SpecificationVersion.EE10 to ArtifactVersion("4.0.0"),
        )

        private val SUBSTITUTIONS_ADD_RULE = DefaultDependencySubstitutions::class.java.getDeclaredMethod(
            "addSubstitution",
            Action::class.java,
            Boolean::class.java
        ).apply { isAccessible = true }
    }

    override fun substitute(configuration: Configuration) {
        val coordinates = listOf(SUN, GLASSFISH)
        val substitutionAction = Action<DependencySubstitution> { details ->
            val requested = details.requested
            if (requested is ModuleComponentSelector) {
                val moduleIdentifier = requested.moduleIdentifier
                val coordinate = ArtifactCoordinate(moduleIdentifier.group, moduleIdentifier.name)
                    .withVersion(requested.version)
                val minVersion = SPECIFICATION_TO_XML_BIND_VERSION[SpecificationVersion.EE10]!!
                if (coordinates.contains(coordinate.module) && coordinate.version < minVersion) {
                    val message = "At least Jakarta EE 10 is required"
                    val target = coordinate.module.withVersion(minVersion)
                    details.useTarget(target.notation, message)
                }
            }
        }
        // See https://github.com/nebula-plugins/gradle-resolution-rules-plugin/blob/06c702c7a5088d924d60e119ac9949cab5cbe719/src/main/kotlin/nebula/plugin/resolutionrules/rules.kt#L202-L210
        SUBSTITUTIONS_ADD_RULE.invoke(configuration.resolutionStrategy.dependencySubstitution, substitutionAction, false)
    }
}
