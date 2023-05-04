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

class PreventTransformSpec extends AbstractPluginSpec {

    def 'transforms cannot be applied to production configurations'() {
        buildFile << """
apply plugin: 'java'

jakartaeeMigration {
    preventTransformsOfProductionConfigurations()
    transform(configurations.implementation)
}
"""

        expect:
        def result = resolvedRuntimeClasspathFailureResult()
        result.output.contains('Use of transforms on production configurations is not allowed (configuration: implementation)')
    }

    def 'migrating java-library does not apply transforms to production configurations'() {
        buildFile << """
apply plugin: 'java-library'

jakartaeeMigration {
    preventTransformsOfProductionConfigurations()
    migrate()
}
"""

        expect:
        resolvedRuntimeClasspathResult()
    }

}
