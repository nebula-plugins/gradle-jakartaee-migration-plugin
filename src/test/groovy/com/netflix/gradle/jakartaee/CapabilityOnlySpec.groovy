package com.netflix.gradle.jakartaee

class CapabilityOnlySpec extends AbstractPluginSpec {
    def 'jakarta.servlet-api is preferred'() {
        buildFile << """
dependencies {
    implementation 'javax.servlet:servlet-api:2.2'
    implementation 'javax.servlet:javax.servlet-api:3.0.1'
    implementation 'jakarta.servlet:jakarta.servlet-api:4.0.2'
}

jakartaeeMigration {
    configureCapabilities()
}
"""

        expect:
        def result = resolvedRuntimeClasspathFailureResult()
        result.output.contains("Cannot select module with conflict on capability 'com.netflix.gradle.jakartaee:servlet-api:3.0.1' also provided by [javax.servlet:servlet-api:2.2(runtime), javax.servlet:servlet-api:2.2(runtime), jakarta.servlet:jakarta.servlet-api:4.0.2(runtime)]")
    }
}
