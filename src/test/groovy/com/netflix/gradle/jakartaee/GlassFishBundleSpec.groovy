package com.netflix.gradle.jakartaee

class GlassFishBundleSpec extends AbstractMigrationSpec {
    def 'org.glassfish:javax.json is preferred regardless of version'() {
        buildFile << """
dependencies {
    implementation 'javax.json:javax.json-api:1.1.4'
    implementation 'org.glassfish:javax.json:1.0'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'org.glassfish:javax.json:1.0'
    }

    def 'org.glassfish:jakarta.json is preferred regardless of version'() {
        buildFile << """
dependencies {
    implementation 'jakarta.json:jakarta.json-api:2.1.1'
    implementation 'org.glassfish:jakarta.json:1.1.5'
}
"""

        expect:
        def coordinates = resolvedRuntimeClasspathCoordinates()
        coordinates[0] == 'org.glassfish:jakarta.json:1.1.5'
    }
}
