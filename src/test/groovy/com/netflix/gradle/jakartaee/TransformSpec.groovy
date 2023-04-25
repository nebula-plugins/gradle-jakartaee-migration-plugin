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

class TransformSpec extends AbstractMigrationSpec {
    def 'artifacts are transformed'() {
        buildFile << """
dependencies {   
    implementation 'ch.qos.reload4j:reload4j:1.2.22' // javax.mail

    // Artifacts that should not be transformed
    implementation 'com.google.guava:guava:31.1-jre' // Findbugs annotations
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 8

        def transformed = files.findAll { it.path.contains("/caches/transforms-") }
        transformed.size() == 1
    }

    def 'javaee api artifacts are not transformed when specifications are excluded'() {
        buildFile << """
dependencies {
${ApiSpec.JAVAEE7_DEPENDENCIES}
}

jakartaeeMigration {
    excludeSpecificationsTransform()
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 33
        files.findAll { it.path.contains("/caches/transforms-") }.isEmpty()
    }

    def 'artifacts can be excluded'() {
        buildFile << """
dependencies {   
    implementation 'ch.qos.reload4j:reload4j:1.2.22' // javax.mail
}

jakartaeeMigration {
    excludeTransform('ch.qos.reload4j:reload4j')
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 1
        files.findAll { it.path.contains("/caches/transforms-") }.isEmpty()
    }


    def 'artifacts can be included'() {
        buildFile << """
dependencies {
    implementation 'ch.qos.reload4j:reload4j:1.2.22' // javax.mail
}

jakartaeeMigration {
    includeTransform('ch.qos.reload4j:reload4j')
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 1

        def transformed = files.findAll { it.path.contains("/caches/transforms-") }
        transformed.size() == 1
    }

}
