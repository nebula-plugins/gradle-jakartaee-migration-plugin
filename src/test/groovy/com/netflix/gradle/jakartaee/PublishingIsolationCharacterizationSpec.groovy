/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.netflix.gradle.jakartaee

import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.TempDir

import static com.netflix.gradle.jakartaee.IsolatedProjectsFixture.*

/** Passing tests below assert a Gradle limitation, not publishing isolation support. */
class PublishingIsolationCharacterizationSpec extends Specification {
    @TempDir File root

    def 'expected Gradle 8.6 child publication isolation failure with Jakarta plugin applied = #applied'() {
        given:
        settings(root, [':library'])
        write(root, 'library/build.gradle', """
            plugins {
                id 'java-library'
                id 'maven-publish'
                ${applied ? "id 'com.netflix.nebula.jakartaee-migration'" : ''}
            }
            group = 'fixture.publication'
            version = '1.0'
            ${applied ? 'jakartaeeMigration { migrate() }' : ''}
            def javaComponent = components.java
            def publicationDirectory = layout.projectDirectory.dir('published')
            publishing {
                publications.create('mavenJava', MavenPublication) { from javaComponent }
                repositories { maven { name = 'validation'; url = publicationDirectory } }
            }
        """)
        def publish = ':library:publishMavenJavaPublicationToValidationRepository'

        when:
        def ordinary = run(root, '8.6', false, [publish])
        def replay = run(root, '8.6', false, [publish])
        def isolated = runner(root, '8.6', true, [publish]).buildAndFail()

        then:
        stored(ordinary)
        reused(replay)
        ordinary.task(publish).outcome == TaskOutcome.SUCCESS
        replay.task(publish).outcome == TaskOutcome.SUCCESS
        isolated.output.contains('Isolated projects is an incubating feature')
        isolated.output.contains("Cannot access project ':' from project ':library'")
        isolated.output.contains('Configuration cache entry discarded')
        !isolated.output.contains('Configuration cache entry stored')
        !isolated.output.contains('Configuration cache entry reused')

        where:
        applied << [false, true]
    }
}
