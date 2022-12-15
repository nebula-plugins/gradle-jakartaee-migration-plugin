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

class ApiSpec extends AbstractMigrationSpec {
    public static JAVAEE7_DEPENDENCIES = """
    // These artifacts provide optional dependencies so we need to list them manually

    // Core profile - javax:javaee-api:7.0
    
    implementation 'org.glassfish:javax.faces:2.2.0'
    implementation 'javax.jms:javax.jms-api:2.0'
    implementation 'com.sun.mail:javax.mail:1.5.0'
    implementation 'javax.resource:javax.resource-api:1.7'
    implementation 'javax.jws:jsr181-api:1.0-MR1'
    implementation 'javax.xml.soap:javax.xml.soap-api:1.3.5'
    implementation 'javax.xml.ws:jaxws-api:2.2.8'
    implementation 'javax.xml.bind:jaxb-api-osgi:2.2.7'
    implementation 'javax.management.j2ee:javax.management.j2ee-api:1.1.1'
    implementation 'javax.security.jacc:javax.security.jacc-api:1.5'
    implementation 'javax.security.auth.message:javax.security.auth.message-api:1.1'
    implementation 'javax.enterprise.concurrent:javax.enterprise.concurrent-api:1.0'
    implementation 'javax.batch:javax.batch-api:1.0'
    implementation 'javax.enterprise.deploy:javax.enterprise.deploy-api:1.6'
    implementation 'javax.xml.registry:javax.xml.registry-api:1.0.5'
    implementation 'javax.xml.rpc:javax.xml.rpc-api:1.1.1'
    
    // Web Profile - javax:javaee-web-api:7.0
    implementation 'javax.servlet:javax.servlet-api:3.1.0'
    implementation 'javax.servlet.jsp:javax.servlet.jsp-api:2.3.1'
    implementation 'javax.el:javax.el-api:3.0.0'
    implementation 'javax.servlet.jsp.jstl:javax.servlet.jsp.jstl-api:1.2.1'
    implementation 'org.glassfish:javax.faces:2.2.0'
    implementation 'javax.faces:javax.faces-api:2.2'
    implementation 'javax.ws.rs:javax.ws.rs-api:2.0'
    implementation 'javax.websocket:javax.websocket-api:1.0'
    implementation 'javax.json:javax.json-api:1.0'
    implementation 'javax.annotation:javax.annotation-api:1.2'
    implementation 'javax.ejb:javax.ejb-api:3.2'
    implementation 'javax.transaction:javax.transaction-api:1.2'
    implementation 'org.eclipse.persistence:javax.persistence:2.1.0'
    implementation 'javax.validation:validation-api:1.1.0.Final'
    implementation 'javax.interceptor:javax.interceptor-api:1.2'
    implementation 'javax.enterprise:cdi-api:1.1'
    implementation 'javax.inject:javax.inject:1'
"""
    public static JAKARTAEE10_DEPENDENCIES = """
    // Jakarta EE 10
    implementation 'jakarta.platform:jakarta.jakartaee-api:10.0.0'
    
    // Optional APIs
    implementation 'jakarta.activation:jakarta.activation-api:2.1.0'
    implementation 'jakarta.enterprise.concurrent:jakarta.enterprise.concurrent-api:3.0.1'
    implementation 'jakarta.xml.bind:jakarta.xml.bind-api:4.0.0'
    implementation 'jakarta.xml.soap:jakarta.xml.soap-api:3.0.0'
    implementation 'jakarta.xml.ws:jakarta.xml.ws-api:4.0.0'
    
    // Optional APIs no longer bundled from 9.0 and later
    implementation 'jakarta.enterprise.deploy:jakarta.enterprise.deploy-api:1.7.2'
    implementation 'jakarta.management.j2ee:jakarta.management.j2ee-api:1.1.4'
    implementation 'jakarta.xml.registry:jakarta.xml.registry-api:1.0.10'
    implementation 'jakarta.xml.rpc:jakarta.xml.rpc-api:1.1.4'
    
    // Optional APIs no longer bundled from 10.0 and later
    implementation 'jakarta.jws:jakarta.jws-api:3.0.0'
    
    // Faces implementation
    implementation 'org.glassfish:jakarta.faces:4.0.0'
"""

    def 'javaee-api:7 is completely replaced by jakarta.jakartaee-api:10'() {
        buildFile << """
dependencies {
$JAVAEE7_DEPENDENCIES

$JAKARTAEE10_DEPENDENCIES
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates.size() == 38

        coordinates.findAll { !it.contains('jakarta') }.isEmpty()

        def faces = coordinates.findAll { it.contains('faces') }
        faces.size() == 1
        faces[0] == 'org.glassfish:jakarta.faces:4.0.0'
    }

}
