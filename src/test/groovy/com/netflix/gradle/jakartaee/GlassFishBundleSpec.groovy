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

class GlassFishBundleSpec extends AbstractMigrationSpec {
    def 'org.glassfish:javax.json is preferred regardless of version'() {
        buildFile << """
dependencies {
    implementation 'javax.json:javax.json-api:1.1.4'
    implementation 'org.glassfish:javax.json:1.0'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'org.glassfish:javax.json:1.0'
    }

    def 'org.glassfish:jakarta.json is preferred regardless of version'() {
        buildFile << """
dependencies {
    implementation 'jakarta.json:jakarta.json-api:2.1.1'
    implementation 'org.glassfish:jakarta.json:1.1.5'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'org.glassfish:jakarta.json:1.1.5'
    }
}
