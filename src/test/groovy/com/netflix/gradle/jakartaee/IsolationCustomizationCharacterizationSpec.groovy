/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.netflix.gradle.jakartaee

import spock.lang.Specification
import spock.lang.TempDir

import static com.netflix.gradle.jakartaee.IsolatedProjectsFixture.*

/** Deliberately unsupported consumer customizations: these passing tests assert failed builds. */
class IsolationCustomizationCharacterizationSpec extends Specification {
    @TempDir File root

    def 'expected failure - a foreign Configuration argument violates isolation on Gradle #version'() {
        given:
        new File(root, 'settings.gradle').text = "rootProject.name = 'customization-fixture'\ninclude 'child'"
        new File(root, 'gradle.properties').text = "org.gradle.java.home=${fixtureJavaHome()}\n"
        new File(root, 'child').mkdirs()
        new File(root, 'child/build.gradle').text = "plugins { id 'com.netflix.nebula.jakartaee-migration' }"
        new File(root, 'build.gradle').text = '''
            plugins { id 'com.netflix.nebula.jakartaee-migration' }
            // Deliberate cross-project customization, not fixture-wide plugin application.
            def foreign = project(':child').configurations.create('foreign')
            jakartaeeMigration.transform(foreign)
            assert foreign.attributes.getAttribute(Attribute.of('com.netflix.gradle.jakartaee', Boolean)) == true
        '''.stripIndent()

        when:
        def ordinary = runner(root, version, false, ['help']).build()
        def ordinaryReplay = runner(root, version, false, ['help']).build()
        def isolated = runner(root, version, true, ['help']).buildAndFail()

        then:
        ordinary.output.contains('Configuration cache entry stored')
        ordinaryReplay.output.contains('Configuration cache entry reused')
        isolated.output.contains(version == '8.6' ? "Cannot access project ':child' from project ':'" :
            "cannot access 'Project.configurations' functionality on another project ':child'")
        !isolated.output.contains('Configuration cache entry stored')
        !isolated.output.contains('Configuration cache entry reused')

        where:
        version << VERSIONS
    }
}
