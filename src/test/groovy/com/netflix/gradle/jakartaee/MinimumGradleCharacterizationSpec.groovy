/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.netflix.gradle.jakartaee

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.TempDir

/** Expected-failure characterization of the README floor, NOT proof of IP compatibility. */
@Requires({ System.getenv('JAVA8_HOME') != null })
class MinimumGradleCharacterizationSpec extends Specification {
    @TempDir File projectDirectory

    def setup() {
        // Tooling API selects its daemon before interpreting build command arguments.
        new File(projectDirectory, 'gradle.properties').text =
            "org.gradle.java.home=${System.getenv('JAVA8_HOME')}\n"
    }

    def 'documented Gradle 4.0 minimum predates configuration cache and ignores the isolation property'() {
        given:
        new File(projectDirectory, 'settings.gradle').text = "rootProject.name = 'minimum-fixture'"
        new File(projectDirectory, 'build.gradle').text = ''

        when:
        def unsupportedCache = runner('help', '--configuration-cache').buildAndFail()

        then:
        unsupportedCache.output.contains("Unknown command-line option '--configuration-cache'")

        when:
        def unsupportedIsolation = runner('help', '-Dorg.gradle.unsafe.isolated-projects=true').build()

        then:
        !unsupportedIsolation.output.contains('Isolated projects is an incubating feature')
        !unsupportedIsolation.output.contains('Configuration cache entry')
    }

    def 'current plugin cannot be applied on the documented Gradle 4.0 minimum'() {
        given:
        new File(projectDirectory, 'settings.gradle').text = "rootProject.name = 'minimum-fixture'"
        new File(projectDirectory, 'build.gradle').text = """
            plugins { id 'com.netflix.nebula.jakartaee-migration' }
        """.stripIndent()

        when:
        def result = runner('help', '--stacktrace').withPluginClasspath().buildAndFail()

        then:
        result.output.contains('com.netflix.gradle.jakartaee.JakartaEeMigrationExtension')
        result.output.contains('Could not generate a proxy class')
        // Reflection order varies: both Property and ListProperty are absent in 4.0.
        result.output.contains('java.lang.NoClassDefFoundError: org/gradle/api/provider/')
    }

    private GradleRunner runner(String... arguments) {
        GradleRunner.create().withProjectDir(projectDirectory).withGradleVersion('4.0')
            .withEnvironment(System.getenv() + ['JAVA_HOME': System.getenv('JAVA8_HOME')])
            .withArguments(arguments.toList() + ["-Dorg.gradle.java.home=${System.getenv('JAVA8_HOME')}".toString()])
    }
}
