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

class PluginSpec extends AbstractPluginSpec {
    def 'plugin applies when it is the only plugin applied'() {
        expect:
        runTasks('help')
    }

    def 'migration fails to apply if java plugin is not applied'() {
        buildFile << """
jakartaeeMigration {
    migrate()
}
"""

        expect:
        runTasksAndFail('help')
    }

    def 'enabling transform without the jvm ecosystem plugin does not fail'() {
        buildFile << """
jakartaeeMigration {
    transform(configurations.create('myconfig'))
}
"""

        expect:
        runTasks('help')
    }

}
