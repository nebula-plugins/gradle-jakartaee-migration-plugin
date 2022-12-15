package com.netflix.gradle.jakartaee

import nebula.test.IntegrationTestKitSpec
import org.gradle.testkit.runner.BuildResult

abstract class AbstractPluginSpec extends IntegrationTestKitSpec {
    def setup() {
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
"""
    }

    BuildResult resolvedRuntimeClasspathResult() {
        return runTasks('resolveRuntimeClasspath')
    }

    BuildResult resolvedRuntimeClasspathFailureResult() {
        return runTasksAndFail('resolveRuntimeClasspath')
    }

    List<String> resolvedRuntimeClasspathCoordinates() {
        runTasks('resolveRuntimeClasspath')
        return new File(projectDir, 'build/runtimeClasspath-coordinates.txt').text.split('\n')
    }

    List<File> resolvedRuntimeClasspathFiles() {
        runTasks('resolveRuntimeClasspath', '--info')
        return new File(projectDir, 'build/runtimeClasspath-files.txt')
                .text
                .split('\n')
                .findAll { !it.isEmpty() }
                .collect { new File(it) }
    }
}
