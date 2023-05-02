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

import spock.lang.Ignore

class SubstituteSpec extends AbstractMigrationSpec {

    def 'jakarta.servlet-api substitutes javax.servlet-api'() {
        debug = true

        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:6.0.0'
    }

    def 'jakarta.servlet-api substitutes javax.servlet-api, upgrading EE8 artifact'() {
        debug = true

        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'jakarta.servlet:jakarta.servlet-api:4.0.2'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:6.0.0'
    }

    @Ignore('requires a later version than 6.0.0 now we have an EE10 baseline')
    def 'jakarta.servlet-api substitutes javax.servlet-api, but does not affect conflict resolution'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'jakarta.servlet:jakarta.servlet-api:6.0.0'
}

jakartaeeMigration {
    substitute()
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'jakarta.servlet:jakarta.servlet-api:6.0.0'
    }

    def 'substitute all javax'() {
        buildFile << """
dependencies {
    implementation 'javax.interceptor:javax.interceptor-api'
    implementation 'javax.xml.ws:jaxws-api'
    implementation 'javax.json.bind:javax.json.bind-api'
    implementation 'javax.xml.registry:javax.xml.registry-api'
    implementation 'javax.inject:javax.inject'
    implementation 'javax.servlet.jsp.jstl:javax.servlet.jsp.jstl-api'
    implementation 'javax.websocket:javax.websocket-client-api'
    implementation 'javax.security.jacc:javax.security.jacc-api'
    implementation 'javax.jms:javax.jms-api'
    implementation 'javax.el:javax.el-api'
    implementation 'javax.validation:validation-api'
    implementation 'javax.xml.soap:javax.xml.soap-api'
    implementation 'javax.ejb:ejb'
    implementation 'javax.servlet.jsp:javax.servlet.jsp-api'
    implementation 'javax.enterprise.deploy:javax.enterprise.deploy-api'
    implementation 'javax.activation:javax.activation-api'
    implementation 'javax.jws:javax.jws-api'
    implementation 'javax.security.auth.message:javax.security.auth.message-api'
    implementation 'javax.json:javax.json-api'
    implementation 'javax.websocket:javax.websocket-api'
    implementation 'javax.persistence:javax.persistence-api'
    implementation 'javax.annotation:javax.annotation-api'
    implementation 'javax.resource:javax.resource-api'
    implementation 'javax.faces:javax.faces-api'
    implementation 'javax.xml.rpc:javax.xml.rpc-api'
    implementation 'javax.ws.rs:javax.ws.rs-api'
    implementation 'javax.security.enterprise:javax.security.enterprise-api'
    implementation 'javax.batch:javax.batch-api'
    implementation 'javax.enterprise:cdi-api'
    implementation 'javax.transaction:javax.transaction-api'
    implementation 'javax.xml.bind:jaxb-api'
    implementation 'javax.servlet:javax.servlet-api'
    implementation 'javax.mail:mail-api'
    implementation 'javax.enterprise.concurrent:javax.enterprise.concurrent-api'
}

jakartaeeMigration {
    substitute()
}
"""
        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.size() == 35

        coordinates.findAll { !it.contains('jakarta') }.isEmpty()
    }

}
