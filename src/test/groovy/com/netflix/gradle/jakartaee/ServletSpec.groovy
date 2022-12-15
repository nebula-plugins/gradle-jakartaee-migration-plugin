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

class ServletSpec extends AbstractMigrationSpec {
    def 'jakarta.servlet-api is preferred'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'javax.servlet:javax.servlet-api:3.0.1'
    implementation 'jakarta.servlet:jakarta.servlet-api:4.0.2'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:4.0.2'
    }

    def 'tomcat-servlet-api is preferred when it provides a higher implementation version'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
    implementation 'org.apache.tomcat:tomcat-servlet-api:9.0.1'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'org.apache.tomcat:tomcat-servlet-api:9.0.1'
    }

    def 'tomcat-servlet-api is not preferred when it provides an equal implementation version'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:4.0.0'
    implementation 'org.apache.tomcat:tomcat-servlet-api:9.0.1'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'javax.servlet:javax.servlet-api:4.0.0'
    }

    def 'tomcat-servlet-api is not preferred when it provides an lower implementation version'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:4.0.0'
    implementation 'org.apache.tomcat:tomcat-servlet-api:8.5.0'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'javax.servlet:javax.servlet-api:4.0.0'
    }

    def 'tomcat-embed-core takes precedence regardless of implementation version provided'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:4.0.0'
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:8.5.0'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'org.apache.tomcat.embed:tomcat-embed-core:8.5.0'
    }
}
