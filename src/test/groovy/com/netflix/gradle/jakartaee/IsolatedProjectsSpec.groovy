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

/** Independent of AbstractMigrationSpec: each fixture opts into exactly the DSL under test. */
class IsolatedProjectsSpec extends Specification {
    @TempDir File temporaryDirectory

    def 'migration preserves behavior and invalidates inputs in #shape projects on Gradle #version'() {
        given:
        def observed = [:]

        when:
        [false, true].each { isolated ->
            def root = directory(isolated ? 'isolated' : 'ordinary')
            def appPath = shape == 'single' ? ':' : shape == 'multi' ? ':app' : ':group:app'
            def libPath = shape == 'multi' ? ':lib' : ':group:lib'
            def app = projectDirectory(root, appPath)
            def lib = projectDirectory(root, libPath)
            settings(root, shape == 'single' ? [] : [appPath, libPath])
            repository(root)
            if (shape != 'single') {
                write(root, 'build.gradle', "plugins { id 'com.netflix.nebula.jakartaee-migration' }")
                if (shape == 'nested') {
                    write(root, 'group/build.gradle', "plugins { id 'com.netflix.nebula.jakartaee-migration' }")
                }
                write(lib, 'build.gradle', javaProject(root) + '''
                    dependencies { api 'fixture:legacy:1.0' }
                    jakartaeeMigration {
                        migrate()
                        excludeTransform('fixture:legacy')
                    }
                ''')
                write(lib, 'src/main/java/fixture/Peer.java', 'package fixture; public class Peer {}')
                write(app, 'src/main/java/fixture/Consumer.java', 'package fixture; public class Consumer { Peer peer; }')
            }
            properties(root, 'legacyVersion=1.0\n')
            write(app, 'build.gradle', javaProject(root) + """
                def legacyVersion = providers.gradleProperty('legacyVersion')
                dependencies {
                    implementation 'javax.servlet:javax.servlet-api:3.1.0'
                    implementation 'jakarta.servlet:jakarta.servlet-api:5.0.0'
                    implementation "fixture:legacy:\${legacyVersion.get()}"
                    ${shape == 'single' ? '' : "implementation project('${libPath}')"}
                }
                // Exercise the lazy source-set callback after migrate() has registered it.
                jakartaeeMigration { migrate() }
                sourceSets.create('extra')
                dependencies { extraImplementation 'javax.servlet:javax.servlet-api:3.1.0' }
                tasks.register('verifyExtra', MigrationReport) {
                    artifacts.from(configurations.extraRuntimeClasspath)
                    report.set(layout.buildDirectory.file('extra.txt'))
                }
            """)
            def tasks = [task(appPath, 'verifyMigration'), task(appPath, 'verifyExtra'), task(appPath, 'classes')]
            if (shape != 'single') tasks.add(task(libPath, 'verifyMigration'))

            def first = run(root, version, isolated, tasks)
            stored(first)
            assert first.task(task(appPath, 'verifyMigration')).outcome == TaskOutcome.SUCCESS
            assert first.task(task(appPath, 'classes')).outcome in [TaskOutcome.SUCCESS, TaskOutcome.UP_TO_DATE]
            if (shape != 'single') assert first.task(task(libPath, 'jar')).outcome == TaskOutcome.SUCCESS
            def baseline = report(app)
            assert baseline.contains('jakarta.servlet-api-6.0.0.jar')
            assert !baseline.contains('javax.servlet-api-3.1.0.jar')
            assert !baseline.contains('jakarta.servlet-api-5.0.0.jar')
            assert baseline.contains('legacy-1.0-jakartaee.jar: type=jakarta.mail.Session; marker=one')
            assert new File(app, 'build/extra.txt').text.contains('jakarta.servlet-api-6.0.0.jar')
            if (shape != 'single') {
                assert report(lib).contains('legacy-1.0.jar: type=javax.mail.Session; marker=one')
                assert baseline.contains('lib.jar')
            }

            new File(app, 'build/migration.txt').delete()
            def replay = run(root, version, isolated, tasks)
            reused(replay)
            assert replay.task(task(appPath, 'verifyMigration')).outcome == TaskOutcome.SUCCESS
            assert report(app) == baseline

            new File(app, 'build.gradle') << "\njakartaeeMigration { excludeTransform('fixture:legacy') }\n"
            def changedDsl = run(root, version, isolated, tasks)
            stored(changedDsl)
            assert changedDsl.output.contains('cannot be reused')
            def excluded = report(app)
            assert excluded.contains('legacy-1.0.jar: type=javax.mail.Session; marker=one')
            assert !excluded.contains('legacy-1.0-jakartaee.jar')
            reused(run(root, version, isolated, tasks))

            properties(root, 'legacyVersion=2.0\n')
            def changedInput = run(root, version, isolated, tasks)
            stored(changedInput)
            assert changedInput.output.contains('cannot be reused')
            def changed = report(app)
            assert changed.contains('legacy-2.0.jar: type=javax.mail.Session; marker=two')
            assert !changed.contains('legacy-1.0')
            reused(run(root, version, isolated, tasks))
            observed[isolated] = [baseline, excluded, changed]
        }
        then:
        observed[false] == observed[true]

        where:
        [version, shape] << [VERSIONS, ['single', 'multi', 'nested']].combinations()
    }

    def 'independent #operation behavior stores and reuses cache on Gradle #version'() {
        given:
        def observed = [:]

        when:
        [false, true].each { isolated ->
            def root = directory(isolated ? 'isolated' : 'ordinary')
            settings(root, [])
            repository(root)
            write(root, 'build.gradle', javaProject(root) + """
                dependencies {
                    implementation 'javax.servlet:javax.servlet-api:3.1.0'
                    ${operation == 'capabilities' ? "implementation 'jakarta.servlet:jakarta.servlet-api:5.0.0'" : ''}
                    implementation 'fixture:legacy:1.0'
                }
                jakartaeeMigration { ${dsl} }
            """)
            stored(run(root, version, isolated, ['verifyMigration']))
            def result = report(root)
            assert result.contains(expected)
            assert result.contains(transformed ? 'legacy-1.0-jakartaee.jar: type=jakarta.mail.Session' :
                'legacy-1.0.jar: type=javax.mail.Session')
            new File(root, 'build/migration.txt').delete()
            reused(run(root, version, isolated, ['verifyMigration']))
            assert report(root) == result
            observed[isolated] = result
        }
        then:
        observed[false] == observed[true]

        where:
        [version, operation, dsl, expected, transformed] << VERSIONS.collectMany { v ->
            [
                [v, 'substitution', 'substitute()', 'jakarta.servlet-api-6.0.0.jar', false],
                [v, 'capabilities', "resolveCapabilityConflicts('runtimeClasspath')", 'jakarta.servlet-api-5.0.0.jar', false],
                [v, 'named migration', "migrate('runtimeClasspath')", 'jakarta.servlet-api-6.0.0.jar', true],
                [v, 'configuration migration', 'migrate(configurations.runtimeClasspath)', 'jakarta.servlet-api-6.0.0.jar', true],
                [v, 'in-memory transform', "transform(); includeTransform('fixture:legacy'); transformInMemory()", 'javax.servlet-api-3.1.0.jar', true],
            ]
        }
    }

    def 'root-only application does not enroll children on Gradle #version'() {
        expect:
        [false, true].each { isolated ->
            def root = directory(isolated ? 'isolated' : 'ordinary')
            settings(root, [':child'])
            write(root, 'build.gradle', "plugins { id 'com.netflix.nebula.jakartaee-migration' }")
            write(root, 'child/build.gradle', '''
                plugins { id 'java-library' }
                assert !plugins.hasPlugin('com.netflix.nebula.jakartaee-migration')
                assert extensions.findByName('jakartaeeMigration') == null
            ''')
            stored(run(root, version, isolated, [':child:jar']))
            reused(run(root, version, isolated, [':child:jar']))
        }

        where:
        version << VERSIONS
    }

    def '#operation preserves explicit capability resolution semantics on Gradle #version'() {
        expect:
        [false, true].each { isolated ->
            def root = directory(isolated ? 'isolated' : 'ordinary')
            settings(root, [])
            repository(root)
            module(root, 'org.apache.tomcat.embed', 'tomcat-embed-core', '10.1.7', 'type=jakarta.servlet.Servlet')
            def buildScript = javaProject(root) + """
                dependencies {
                    implementation 'javax.servlet:javax.servlet-api:3.1.0'
                    implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.7'
                    implementation 'fixture:legacy:1.0'
                }
                jakartaeeMigration {
                    configureCapabilities()
                    ${dsl}
                }
            """
            if (operation != 'automatic migration') {
                // These overloads substitute/transform but do not install a conflict resolver.
                // Keep the expected failure separate from the successful cache lifecycle.
                def unresolved = directory("unresolved-${isolated}")
                settings(unresolved, [])
                write(unresolved, 'build.gradle', buildScript)
                def conflict = runner(unresolved, version, isolated, ['verifyMigration']).buildAndFail()
                assert conflict.output.contains('servlet-api')
                assert conflict.output.contains('tomcat-embed-core')
                // Gradle 9.8 changed the sentence around this capability-conflict diagnostic.
                assert conflict.output.contains('Cannot select module')
                assert conflict.output.contains("capability 'com.netflix.gradle.jakartaee:servlet-api:")
                buildScript += "\njakartaeeMigration { ${resolver} }\n"
            }
            write(root, 'build.gradle', buildScript)
            stored(run(root, version, isolated, ['verifyMigration']))
            def resolved = report(root)
            assert resolved.contains('tomcat-embed-core-10.1.7.jar')
            assert !resolved.contains('servlet-api-')
            assert resolved.contains('legacy-1.0-jakartaee.jar: type=jakarta.mail.Session')
            new File(root, 'build/migration.txt').delete()
            reused(run(root, version, isolated, ['verifyMigration']))
            assert report(root) == resolved
        }

        where:
        [version, operation, dsl, resolver] << VERSIONS.collectMany { v ->
            [
                [v, 'automatic migration', 'migrate()', ''],
                [v, 'named migration', "migrate('runtimeClasspath')", "resolveCapabilityConflicts('runtimeClasspath')"],
                [v, 'configuration migration', 'migrate(configurations.runtimeClasspath)', 'resolveCapabilityConflicts(configurations.runtimeClasspath)'],
            ]
        }
    }

    private File directory(String name) {
        def result = new File(temporaryDirectory, name)
        result.mkdirs()
        result
    }
}
