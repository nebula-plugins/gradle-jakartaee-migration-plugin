package com.netflix.gradle.jakartaee

import nebula.test.IntegrationTestKitSpec

abstract class JakartaEeMigrationPluginSpec extends IntegrationTestKitSpec {
    def setup() {
        debug = true

        buildFile << """
plugins {
    id 'java'
    id 'com.netflix.nebula.jakartaee-migration'
}

tasks.register('resolveRuntimeClasspath') {
    doFirst {
        def coordinates = configurations.runtimeClasspath.resolvedConfiguration.resolvedArtifacts.collect {
            def id = it.moduleVersion.id
            "\${id.group}:\${id.name}:\${id.version}"
        }.join('\\n')
        def files = configurations.runtimeClasspath.files.join('\\n')
        
        buildDir.mkdirs()
        new File(buildDir, 'runtimeClasspath-coordinates.txt').write(coordinates)
        new File(buildDir, 'runtimeClasspath-files.txt').write(files)
    }
}

repositories {
    maven {
        url "https://repo1.maven.org/maven2"
        metadataSources {
            mavenPom()
             // Avoid Gradle metadata so we can resolve Spring 6 artifacts on JDK 8
            ignoreGradleMetadataRedirection()
        }
    }
}

jakartaeeMigration {
    migrateResolvableConfigurations()
}
"""
    }

    List<String> resolvedRuntimeClasspathCoordinates() {
        runTasks('resolveRuntimeClasspath')
        return new File(projectDir, 'build/runtimeClasspath-coordinates.txt').text.split('\n')
    }

    List<File> resolvedRuntimeClasspathFiles() {
        runTasks('resolveRuntimeClasspath')
        return new File(projectDir, 'build/runtimeClasspath-files.txt')
                .text
                .split('\n')
                .collect { new File(it) }
    }
}
