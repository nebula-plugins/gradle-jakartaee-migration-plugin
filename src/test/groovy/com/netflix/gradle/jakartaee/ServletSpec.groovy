package com.netflix.gradle.jakartaee

class ServletSpec extends JakartaEeMigrationPluginSpec {
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
