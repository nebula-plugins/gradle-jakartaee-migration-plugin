package com.netflix.gradle.jakartaee

class TransformSpec extends JakartaEeMigrationPluginSpec {
    def 'artifacts are transformed'() {
        buildFile << """
dependencies {   
    implementation 'ch.qos.reload4j:reload4j:1.2.22' // javax.mail

    // Artifacts that should not be transformed
    implementation 'com.google.guava:guava:31.1-jre' // Findbugs annotations
    implementation 'org.springframework:spring-context:6.0.0' // spring-context, spring-beans have intentional references to javax
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 14

        def transformed = files.findAll { it.path.contains("/caches/transforms-") }
        transformed.size() == 1
    }

    def 'javaee api artifacts are not transformed'() {
        buildFile << """
dependencies {
${ApiSpec.JAVAEE7_DEPENDENCIES}
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 32
        files.findAll { it.path.contains("/caches/transforms-") }.isEmpty()
    }

    def 'artifacts can be excluded'() {
        buildFile << """
dependencies {   
    implementation 'ch.qos.reload4j:reload4j:1.2.22' // javax.mail
}

jakartaeeMigration {
    exclude('ch.qos.reload4j:reload4j')
}
"""

        expect:
        def files = resolvedRuntimeClasspathFiles()
        files.size() == 1
        files.findAll { it.path.contains("/caches/transforms-") }.isEmpty()
    }
}
