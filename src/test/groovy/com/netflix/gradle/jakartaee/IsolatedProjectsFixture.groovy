/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.netflix.gradle.jakartaee

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

/** Local repositories and task inputs shared by the bounded compatibility checks. */
class IsolatedProjectsFixture {
    // Pin evidence to exact distributions; changing the wrapper must not silently expand claims.
    static final List<String> VERSIONS = ['8.6', '9.6.1']

    static File projectDirectory(File root, String path) {
        path == ':' ? root : new File(root, path.substring(1).replace(':', '/'))
    }

    static String task(String project, String name) {
        project == ':' ? ":${name}" : "${project}:${name}"
    }

    static void settings(File root, List<String> projects) {
        write(root, 'settings.gradle', "rootProject.name = 'isolation-fixture'\n" +
            projects.collect { "include '${it}'" }.join('\n'))
        properties(root, '')
    }

    static void properties(File root, String content) {
        write(root, 'gradle.properties', content + "org.gradle.java.home=${fixtureJavaHome()}\n")
    }

    static String fixtureJavaHome() {
        // The repository also runs tests on JDK 25; Gradle 8.6 itself needs an older JVM.
        // setup-java supplies JAVA_HOME_21_* in that CI matrix. Local callers can use JAVA21_HOME.
        def javaHome = System.getenv('JAVA21_HOME') ?: System.getenv('JAVA_HOME_21_X64') ?:
            System.getenv('JAVA_HOME_21_ARM64') ?: System.getProperty('java.home')
        javaHome.replace('\\', '/')
    }

    static void write(File root, String path, String text) {
        def target = new File(root, path)
        target.parentFile.mkdirs()
        target.text = text.stripIndent()
    }

    static String javaProject(File root) {
        """
            plugins {
                id 'java-library'
                id 'com.netflix.nebula.jakartaee-migration'
            }
            repositories { maven { url = '${new File(root, 'repo').toURI()}' } }
        """ + '''
            abstract class MigrationReport extends DefaultTask {
                @Classpath abstract ConfigurableFileCollection getArtifacts()
                @OutputFile abstract RegularFileProperty getReport()
                @TaskAction void reportArtifacts() {
                    def lines = artifacts.files.collect { file ->
                        if (!file.name.endsWith('.jar')) return file.name
                        new java.util.zip.ZipFile(file).withCloseable { zip ->
                            def entry = zip.getEntry('sample.properties')
                            file.name + (entry == null ? '' : ': ' + zip.getInputStream(entry).getText('UTF-8'))
                        }
                    }.sort()
                    report.get().asFile.text = lines.join('\\n')
                }
            }
            tasks.register('verifyMigration', MigrationReport) {
                artifacts.from(configurations.runtimeClasspath)
                report.set(layout.buildDirectory.file('migration.txt'))
            }
        '''
    }

    static String report(File project) {
        new File(project, 'build/migration.txt').text
    }

    static void repository(File root) {
        module(root, 'javax.servlet', 'javax.servlet-api', '3.1.0', 'type=javax.servlet.Servlet')
        module(root, 'jakarta.servlet', 'jakarta.servlet-api', '5.0.0', 'type=jakarta.servlet.Servlet')
        module(root, 'jakarta.servlet', 'jakarta.servlet-api', '6.0.0', 'type=jakarta.servlet.Servlet')
        module(root, 'fixture', 'legacy', '1.0', 'type=javax.mail.Session; marker=one')
        module(root, 'fixture', 'legacy', '2.0', 'type=javax.mail.Session; marker=two')
    }

    static void module(File root, String group, String name, String version, String content) {
        def path = "repo/${group.replace('.', '/')}/${name}/${version}/${name}-${version}"
        write(root, path + '.pom', """
            <project><modelVersion>4.0.0</modelVersion><groupId>${group}</groupId>
            <artifactId>${name}</artifactId><version>${version}</version></project>
        """)
        new JarOutputStream(new FileOutputStream(new File(root, path + '.jar'))).withCloseable { jar ->
            def entry = new JarEntry('sample.properties')
            entry.time = 0
            jar.putNextEntry(entry)
            jar.write(content.getBytes('UTF-8'))
            jar.closeEntry()
        }
    }

    static GradleRunner runner(File root, String version, boolean isolated, List<String> tasks) {
        assert VERSIONS.contains(version): "Review isolation flags before extending the tested matrix"
        // Both pinned versions predate --isolated-projects / org.gradle.isolated-projects (9.7).
        // Explicit false prevents ambient unsafe isolation settings from contaminating the control.
        // Re-read temporary fixture files each build: macOS watcher notifications can arrive
        // after a rapid TestKit replay, either missing an edit or invalidating one build late.
        GradleRunner.create().withProjectDir(root).withPluginClasspath().withGradleVersion(version)
            .withArguments((tasks + ['--configuration-cache', '--configuration-cache-problems=fail',
                "-Dorg.gradle.unsafe.isolated-projects=${isolated}", '--no-watch-fs', '--stacktrace', '--max-workers=2'])
                .collect { it.toString() })
    }

    static BuildResult run(File root, String version, boolean isolated, List<String> tasks) {
        def result = runner(root, version, isolated, tasks).build()
        if (isolated) assert result.output.toLowerCase(Locale.ROOT).contains('isolated projects is an incubating feature')
        result
    }

    static void stored(BuildResult result) {
        assert result.output.contains('Configuration cache entry stored')
        assert !result.output.contains('Configuration cache entry reused')
    }

    static void reused(BuildResult result) {
        assert result.output.contains('Reusing configuration cache')
        assert result.output.contains('Configuration cache entry reused')
    }
}
