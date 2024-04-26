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

class CapabilityOnlySpec extends AbstractPluginSpec {
    def 'jakarta.servlet-api is preferred'() {
        buildFile << """
apply plugin: 'java'

dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'javax.servlet:javax.servlet-api:3.0.1'
    implementation 'jakarta.servlet:jakarta.servlet-api:4.0.2'
}

jakartaeeMigration {
    configureCapabilities()
}
"""

        expect:
        def result = resolvedRuntimeClasspathFailureResult()
        result.output.contains("Cannot select module with conflict on capability 'com.netflix.gradle.jakartaee:servlet-api:3.0.1' also provided by [javax.servlet:servlet-api:2.2(runtime), jakarta.servlet:jakarta.servlet-api:4.0.2(runtime)]")
    }
}
