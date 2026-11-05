/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.netflix.gradle.jakartaee

import groovy.json.JsonSlurper
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.TempDir

import java.util.zip.ZipFile

import static com.netflix.gradle.jakartaee.IsolatedProjectsFixture.*

class PublishingIsolationSpec extends Specification {
    @TempDir File temporaryDirectory

    def '#operation preserves publications and outgoing variants on Gradle #version'() {
        given:
        def observed = [:]

        when:
        [false, true].each { isolated ->
            def root = new File(temporaryDirectory, isolated ? 'isolated' : 'ordinary')
            settings(root, [':library', ':consumer'])
            repository(root)
            def library = projectDirectory(root, ':library')
            def consumer = projectDirectory(root, ':consumer')
            write(library, 'src/main/java/fixture/Published.java', '''
                package fixture;
                public class Published { public static String namespace() { return "javax.mail.Session"; } }
            ''')
            write(library, 'src/main/resources/sample.properties', 'type=javax.mail.Session; marker=publication')
            write(library, 'build.gradle', javaProject(root) + """
                apply plugin: 'maven-publish'
                group = 'fixture.publication'
                version = '1.0'
                dependencies {
                    api 'fixture:legacy:1.0'
                    implementation 'javax.servlet:javax.servlet-api:3.1.0'
                }
                jakartaeeMigration { ${dsl} }
                // Make binary comparison independent of fixture creation times on both distributions.
                tasks.withType(Jar).configureEach {
                    preserveFileTimestamps = false
                    reproducibleFileOrder = true
                }
                def javaComponent = components.java
                def publicationDirectory = layout.projectDirectory.dir('published')
                publishing {
                    publications.create('mavenJava', MavenPublication) { from javaComponent }
                    repositories { maven { name = 'validation'; url = publicationDirectory } }
                }
            """)
            write(consumer, 'build.gradle', javaProject(root) + '''
                dependencies { implementation project(':library') }
            ''')
            write(consumer, 'src/main/java/fixture/Consumer.java', '''
                package fixture;
                public class Consumer { String namespace = Published.namespace(); }
            ''')
            def tasks = [':library:publishMavenJavaPublicationToValidationRepository', ':library:outgoingVariants',
                         ':library:verifyMigration', ':consumer:verifyMigration', ':consumer:classes']
            def first = run(root, version, isolated, tasks)
            stored(first)
            assert first.task(':library:publishMavenJavaPublicationToValidationRepository').outcome == TaskOutcome.SUCCESS
            assert first.task(':consumer:compileJava').outcome == TaskOutcome.SUCCESS
            def baseline = publication(library)
            def variants = baseline.module.variants.collectEntries { [(it.name): it] }
            assert variants.keySet() == ['apiElements', 'runtimeElements'] as Set
            assert !variants.apiElements.attributes.containsKey('com.netflix.gradle.jakartaee')
            assert variants.runtimeElements.attributes['com.netflix.gradle.jakartaee'] == (automatic ? true : null)
            assert variants.apiElements.dependencies == [[group: 'fixture', module: 'legacy', version: [requires: '1.0']]]
            assert variants.runtimeElements.dependencies as Set == [
                [group: 'fixture', module: 'legacy', version: [requires: '1.0']],
                [group: 'javax.servlet', module: 'javax.servlet-api', version: [requires: '3.1.0']],
            ] as Set
            assert baseline.pom.contains('<groupId>javax.servlet</groupId>')
            assert baseline.pom.contains('<artifactId>legacy</artifactId>')
            assert !baseline.pom.contains('jakarta.servlet')
            assert !baseline.pom.contains('jakartaee')
            assert baseline.resource == 'type=javax.mail.Session; marker=publication'
            assert baseline.module.variants.every { it.files*.name == ['library-1.0.jar'] }
            def libraryReport = report(library)
            assert libraryReport.contains(migrated ? 'legacy-1.0-jakartaee.jar: type=jakarta.mail.Session' :
                'legacy-1.0.jar: type=javax.mail.Session')
            assert libraryReport.contains(migrated ? 'jakarta.servlet-api-6.0.0.jar' : 'javax.servlet-api-3.1.0.jar')
            def consumerReport = report(consumer)
            assert consumerReport.contains('library-1.0.jar: type=javax.mail.Session; marker=publication')
            def outgoing = outgoingVariants(first, root)
            assert outgoing.contains('Variant apiElements')
            assert outgoing.contains('Variant runtimeElements')
            assert outgoing.contains('com.netflix.gradle.jakartaee') == automatic

            // Replay task actions and republish from the cached graph, then compare the actual outputs.
            new File(library, 'build/migration.txt').delete()
            new File(consumer, 'build/migration.txt').delete()
            def replay = run(root, version, isolated, tasks)
            reused(replay)
            assert replay.task(':library:publishMavenJavaPublicationToValidationRepository').outcome == TaskOutcome.SUCCESS
            assert replay.task(':consumer:verifyMigration').outcome == TaskOutcome.SUCCESS
            assert publication(library) == baseline
            assert report(library) == libraryReport
            assert report(consumer) == consumerReport
            assert outgoingVariants(replay, root) == outgoing
            observed[isolated] = [baseline, outgoing, libraryReport, consumerReport]
        }

        then:
        observed[false] == observed[true]

        where:
        // 8.6 cannot isolate Maven publication creation in a child, even without this plugin.
        // PublishingIsolationCharacterizationSpec retains that independent Gradle limitation.
        [version, operation, dsl, automatic, migrated] << ['9.6.1'].collectMany { v ->
            [
                [v, 'default application', '', false, false],
                [v, 'automatic migration', 'migrate()', true, true],
                [v, 'runtime-only migration', "migrate('runtimeClasspath')", false, true],
            ]
        }
    }

    private static Map publication(File library) {
        def published = new File(library, 'published/fixture/publication/library/1.0')
        def metadata = new JsonSlurper().parse(new File(published, 'library-1.0.module'))
        // Gradle's invocation ID is provenance, not outgoing variant behavior.
        metadata.createdBy.gradle.remove('buildId')
        def jar = new File(published, 'library-1.0.jar')
        def resource = new ZipFile(jar).withCloseable { zip ->
            zip.getInputStream(zip.getEntry('sample.properties')).getText('UTF-8')
        }
        [module: metadata, pom: new File(published, 'library-1.0.pom').text,
         jar: jar.bytes.encodeBase64().toString(), resource: resource]
    }

    private static String outgoingVariants(BuildResult result, File root) {
        def taskOutput = result.output.split('> Task :library:outgoingVariants', 2)[1]
            .split('> Task |BUILD SUCCESSFUL', 2)[0]
        taskOutput.replace(root.absolutePath, '<fixture>').trim()
    }
}
