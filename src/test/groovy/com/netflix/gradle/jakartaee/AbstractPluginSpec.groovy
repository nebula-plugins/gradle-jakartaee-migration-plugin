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

import nebula.test.IntegrationTestKitSpec
import org.gradle.testkit.runner.BuildResult

abstract class AbstractPluginSpec extends IntegrationTestKitSpec {
    def setup() {
        buildFile << """
plugins {
    id 'com.netflix.nebula.jakartaee-migration'
}

abstract class ResolveRuntimeClasspath extends DefaultTask {
    @Input
    abstract ListProperty<File> getClasspath() 

    @Input
    abstract SetProperty<ComponentArtifactIdentifier> getArtifactIds()

    @Input
    abstract Property<File> getBuildDirectory()


    @TaskAction
    void resolveRuntimeClasspath() {
        if (!artifactIds.isPresent()) {
            return
        }
        def coordinates = artifactIds.get().collect {
            def id = it.identifier
            "\${id.group}:\${id.name}:\${id.version}"
        }.join('\\n')
        def files = classpath.get().join('\\n')
        
        def buildDir = buildDirectory.get()
        buildDir.mkdirs()
        new File(buildDir, 'runtimeClasspath-coordinates.txt').write(coordinates)
        new File(buildDir, 'runtimeClasspath-files.txt').write(files)
    }
}

tasks.register('resolveRuntimeClasspath', ResolveRuntimeClasspath).configure {
    artifactIds.set(project.configurations.runtimeClasspath.resolvedConfiguration.resolvedArtifacts.moduleVersion)
    
    classpath.set(project.configurations.named('runtimeClasspath').get().files)
    buildDirectory.set(project.layout.buildDirectory.asFile.get())
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

        new File(projectDir, 'gradle.properties') << '''org.gradle.configuration-cache=true'''.stripIndent()
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
