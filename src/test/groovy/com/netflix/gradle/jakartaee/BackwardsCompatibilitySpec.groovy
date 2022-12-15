package com.netflix.gradle.jakartaee

class BackwardsCompatibilitySpec extends AbstractMigrationSpec {
    def 'plugin compatible with gradle #gradleVersion'() {
       when:
       buildFile << """
dependencies {   
    implementation 'ch.qos.reload4j:reload4j:1.2.22' // javax.mail    
}
"""
       def files = resolvedRuntimeClasspathFiles()

       then:
       !files.isEmpty()

       where:
       gradleVersion << ['4.0', '5.6.4', '6.9', '7.6']
    }
}
