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

package com.netflix.gradle.jakartaee

import org.gradle.internal.impldep.com.google.common.io.Files

class MultiProjectSpec extends AbstractPluginSpec {
    def 'runtimeElements can resolve'() {
        debug = true

        def projectA = addSubproject('a')
        def projectB = addSubproject('b')

        def projectABuildFile = new File(projectA, 'build.gradle')
        def projectBBuildFile = new File(projectB, 'build.gradle')

        Files.copy(buildFile, projectABuildFile)
        Files.copy(buildFile, projectBBuildFile)

        buildFile.delete()

        projectABuildFile << """
apply plugin: 'java'

jakartaeeMigration {
    migrate()
}
"""

        projectBBuildFile << """
apply plugin: 'java'

jakartaeeMigration {
    migrate()
}

dependencies {
    implementation project(':a')
}        
"""

        expect:
        resolvedRuntimeClasspathResult()
    }
}
