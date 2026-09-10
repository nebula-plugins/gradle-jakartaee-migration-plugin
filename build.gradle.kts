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

plugins {
    id("com.netflix.nebula.plugin-plugin")
    `kotlin-dsl`
}

dependencies {
    implementation ("org.apache.tomcat:jakartaee-migration:1.0.6:shaded") {
        exclude(group = "*", module = "*")
    }
    testImplementation("org.spockframework:spock-junit4:2.4-groovy-4.0")
    testImplementation(platform("org.junit:junit-bom:5.14.3"))
}

// last version of nebula-test that works on java 8
configurations.named("testRuntimeClasspath") {
    resolutionStrategy {
        force("com.netflix.nebula:nebula-test:11.13.0")
    }
}
configurations.named("testCompileClasspath") {
    resolutionStrategy {
        force("com.netflix.nebula:nebula-test:11.13.0")
    }
}
configurations.named("testArchRulesRuntime") {
    resolutionStrategy {
        force("com.netflix.nebula:nebula-test:11.13.0")
    }
}
description = "Provides Gradle capabilities and transforms to ease the migration from Java EE to Jakarta EE"

contacts {
    addPerson("nebula-plugins-oss@netflix.com") {
        moniker("Nebula Plugins Maintainers")
        github("nebula-plugins")
    }
}

gradlePlugin {
    plugins {
        create("com.netflix.nebula.jakartaee-migration") {
            id = "com.netflix.nebula.jakartaee-migration"
            implementationClass = "com.netflix.gradle.jakartaee.JakartaEeMigrationPlugin"
            displayName = "Gradle Jakarta EE Migration Plugin"
            description = project.description
           tags.set(listOf("nebula", "javaee", "jakartaee"))
        }
    }
}

kotlin {
    explicitApi()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "9.6.1"
    distributionSha256Sum = "61ba77b3ff7167e60962763eb4bae79db7120c189b9544358d0ade3c1e712a83"
}