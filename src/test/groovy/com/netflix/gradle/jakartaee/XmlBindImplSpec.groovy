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

class XmlBindImplSpec extends AbstractCapabilitySpec {
    def 'capability resolution chooses highest version'() {
        when:
        buildFile << """
dependencies {
    implementation 'com.sun.xml.bind:jaxb-impl:4.0.0'
    implementation 'org.glassfish.jaxb:jaxb-runtime:4.0.2'
}
"""
        def coordinates = resolvedRuntimeClasspathCoordinates()

        then:
        coordinates.size() == 7
        coordinates[0] == 'org.glassfish.jaxb:jaxb-runtime:4.0.2'

        and:

        when:
        buildFile << """
dependencies {
    implementation 'org.glassfish.jaxb:jaxb-runtime:4.0.0'
    implementation 'com.sun.xml.bind:jaxb-impl:4.0.2'
}
"""
        coordinates = resolvedRuntimeClasspathCoordinates()

        then:
        coordinates.size() == 5
        coordinates[0] == 'com.sun.xml.bind:jaxb-impl:4.0.2'
    }

    def 'jaxb-impl is substituted'() {
        buildFile << """
dependencies {
    implementation 'com.sun.xml.bind:jaxb-impl:2.3.0'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.size() == 5
        coordinates[0] == 'com.sun.xml.bind:jaxb-impl:4.0.0'
    }

    def 'jaxb-runtime is substituted'() {
        buildFile << """
dependencies {
    implementation 'org.glassfish.jaxb:jaxb-runtime:2.3.0'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.size() == 7
        coordinates[0] == 'org.glassfish.jaxb:jaxb-runtime:4.0.0'
    }

    def 'jaxb-impl is substituted and replaces equally versioned jaxb-runtime'() {
        buildFile << """
dependencies {
    implementation 'com.sun.xml.bind:jaxb-impl:2.3.0'
    implementation 'org.glassfish.jaxb:jaxb-runtime:2.3.0'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.size() == 5
        coordinates[0] == 'com.sun.xml.bind:jaxb-impl:4.0.0'
    }

    def 'substitution does not downgrade'() {
        buildFile << """
dependencies {
    implementation 'com.sun.xml.bind:jaxb-impl:3.0.1'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.size() == 5
        coordinates[0] == 'com.sun.xml.bind:jaxb-impl:4.0.0'
    }
}
