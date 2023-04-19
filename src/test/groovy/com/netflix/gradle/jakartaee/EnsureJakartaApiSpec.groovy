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

class EnsureJakartaApiSpec extends AbstractMigrationSpec {

    def 'jakarta.servlet-api substitutes javax.servlet-api'() {
        debug = true

        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
}

jakartaeeMigration {
    ensureJakartaApi()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:5.0.0'
    }

    def 'jakarta.servlet-api substitutes javax.servlet-api, upgrading EE8 artifact'() {
        debug = true

        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'jakarta.servlet:jakarta.servlet-api:4.0.2'
}

jakartaeeMigration {
    ensureJakartaApi()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:5.0.0'
    }

    def 'jakarta.servlet-api substitutes javax.servlet-api, but does not affect conflict resolution'() {
        debug = true

        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'jakarta.servlet:jakarta.servlet-api:6.0.0'
}

jakartaeeMigration {
    ensureJakartaApi()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:6.0.0'
    }

}
