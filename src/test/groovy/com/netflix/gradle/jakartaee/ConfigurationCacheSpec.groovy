/*
 * Copyright 2025 Netflix, Inc.
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

class ConfigurationCacheSpec extends AbstractMigrationSpec {

    def 'configuration cache is stored and reused with migrate()'() {
        given:
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
}

jakartaeeMigration {
    migrate()
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Calculating task graph')
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Reusing configuration cache')
        !secondRun.output.contains('Calculating task graph')
        secondRun.output.contains('Configuration cache entry reused')
    }

    def 'configuration cache works with transform()'() {
        given:
        buildFile << """
dependencies {
    implementation 'ch.qos.reload4j:reload4j:1.2.22'
}

jakartaeeMigration {
    transform()
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')

        and:
        def files = resolvedRuntimeClasspathFiles()
        def transformed = files.findAll { it.path.contains("/transformed/") }
        transformed.size() >= 1
    }

    def 'configuration cache works with substitute()'() {
        given:
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
}

jakartaeeMigration {
    substitute()
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')

        and:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.any { it.contains('jakarta.servlet:jakarta.servlet-api') }
        coordinates.every { !it.contains('javax.servlet:javax.servlet-api') }
    }

    def 'configuration cache works with resolveCapabilityConflicts()'() {
        given:
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
    implementation 'jakarta.servlet:jakarta.servlet-api:5.0.0'
}

jakartaeeMigration {
    resolveCapabilityConflicts('runtimeClasspath')
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')

        and:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        // Should resolve to jakarta.servlet-api (higher version wins)
        coordinates.any { it.contains('jakarta.servlet:jakarta.servlet-api') }
        // javax.servlet-api should be excluded due to capability conflict
        coordinates.every { !it.contains('javax.servlet:javax.servlet-api:3.1.0') }
    }

    def 'configuration cache works with named configuration methods'() {
        given:
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
}

jakartaeeMigration {
    migrate('runtimeClasspath')
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')
    }

    def 'configuration cache works with excludeTransform()'() {
        given:
        buildFile << """
dependencies {
    implementation 'ch.qos.reload4j:reload4j:1.2.22'
    implementation 'com.google.guava:guava:31.1-jre'
}

jakartaeeMigration {
    transform()
    excludeTransform('ch.qos.reload4j:reload4j')
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')

        and:
        def files = resolvedRuntimeClasspathFiles()
        // reload4j should not be transformed (explicitly excluded)
        // guava should not be transformed (no javax references)
        def transformed = files.findAll { it.path.contains("/transformed/") }
        transformed.size() == 0
    }

    def 'configuration cache works with includeTransform()'() {
        given:
        buildFile << """
dependencies {
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.7'
}

jakartaeeMigration {
    transform()
    includeTransform('org.apache.tomcat.embed:tomcat-embed-core')
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')
    }

    def 'configuration cache works with excludeSpecificationsTransform()'() {
        given:
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
    implementation 'ch.qos.reload4j:reload4j:1.2.22'
}

jakartaeeMigration {
    transform()
    excludeSpecificationsTransform()
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')

        and:
        def files = resolvedRuntimeClasspathFiles()
        def transformed = files.findAll { it.path.contains("/transformed/") }
        // reload4j should be transformed, but javax.servlet-api should not (it's a specification)
        transformed.size() == 1
        transformed[0].name.contains('reload4j')
    }

    def 'configuration cache works with preventTransformsOfProductionConfigurations()'() {
        given:
        buildFile << """
dependencies {
    implementation 'ch.qos.reload4j:reload4j:1.2.22'
}

jakartaeeMigration {
    preventTransformsOfProductionConfigurations()
}
"""

        when:
        def firstRun = runTasks('help')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('help')

        then:
        secondRun.output.contains('Configuration cache entry reused')
    }

    def 'configuration cache works with transformInMemory()'() {
        given:
        writeHelloWorld()
        buildFile << """
dependencies {
    implementation 'ch.qos.reload4j:reload4j:1.2.22'
}

jakartaeeMigration {
    transform()
    transformInMemory()
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        def secondRun = runTasks('resolveRuntimeClasspath')

        then:
        secondRun.output.contains('Configuration cache entry reused')
    }

    def 'configuration cache invalidates when extension configuration changes'() {
        given:
        buildFile << """
dependencies {
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
}

jakartaeeMigration {
    substitute()
}
"""

        when:
        def firstRun = runTasks('resolveRuntimeClasspath')

        then:
        firstRun.output.contains('Configuration cache entry stored')

        when:
        buildFile.text = buildFile.text.replace('substitute()', 'migrate()')
        def thirdRun = runTasks('resolveRuntimeClasspath')

        then:
        thirdRun.output.contains('Configuration cache entry stored')
        !thirdRun.output.contains('Configuration cache entry reused')
    }
}
