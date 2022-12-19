# Gradle Jakarta EE Migration Plugin

![Support Status](https://img.shields.io/badge/nebula-active-green.svg)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com.netflix.nebula/gradle-jakartaee-migration-plugin/maven-metadata.xml.svg?label=gradlePluginPortal)](https://plugins.gradle.org/plugin/com.netflix.nebula.jakartaee-migration)
[![Maven Central](https://img.shields.io/maven-central/v/com.netflix.nebula/gradle-jakartaee-migration-plugin)](https://maven-badges.herokuapp.com/maven-central/com.netflix.nebula/gradle-jakartaee-migration-plugin)
![Build](https://github.com/nebula-plugins/gradle-jakartaee-migration-plugin/actions/workflows/nebula.yml/badge.svg)
[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/gradle-jakartaee-migration-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Provides Gradle capabilities and transforms to ease the migration from Java EE to Jakarta EE.

## Features

- [Capabilities](https://docs.gradle.org/current/userguide/component_capabilities.html) to cause legacy and modern API artifacts of the EE specification to conflict
- [Capabilities Resolution](https://docs.gradle.org/current/userguide/dependency_capability_conflict.html) to resolve those capability conflicts by selecting the artifact providing the highest specification version:
- [Artifact Transforms](https://docs.gradle.org/current/userguide/artifact_transforms.html) to automate the migration of `javax` to `jakarta` packages for legacy artifacts, avoiding needing to fork or manage multiple release lines for these libraries 
- Compatible with Gradle 4.0 and later

### Capabilities

Capabilities are configured for every [EE specification](src/main/kotlin/com/netflix/gradle/jakartaee/specifications). They require that only one artifact provide a given EE specification API:   

```
Cannot select module with conflict on capability 'com.netflix.gradle.jakartaee:servlet-api:3.0.1' also provided by [javax.servlet:servlet-api:2.2(runtime), javax.servlet:servlet-api:2.2(runtime), jakarta.servlet:jakarta.servlet-api:4.0.2(runtime)]
```

### Capabilities Resolution

The default capability resolution strategy is to select the artifact that provides the highest version of the specification, with special handling for certain types of artifacts:

- Tomcat and GlassFish release versions, which use their own version scheme, are translated from release version to specification version
- Bundle artifacts such as `org.apache.tomcat.embed:tomcat-embed-core` and `org.glassfish:jakarta.json` that contain both the EE API and implementation are preferred regardless of specification version provided 

### Transform

Transforms are implemented using the [tomcat-jakartaee-migration](https://github.com/apache/tomcat-jakartaee-migration) tool. The benefit of using transforms over packaging or deployment time conversion, is that dependencies are transformed and used at build, test, development and runtime, avoiding the potential for unexpected runtime side effects that weren't seen during development.

The transform eases migration in ecosystems with legacy libraries that will not get `jakarta` compatible releases, or internal libraries that need to retain `javax` package references while applications roman-ride both package namespaces and multiple release lines would be prohibitive to maintain.

```
Migration finished for archive [guava-31.1-jre.jar]
Migration completed successfully in [427] milliseconds
No JakartaEE transformation required for guava-31.1-jre.jar
Transforming reload4j-1.2.22.jar (ch.qos.reload4j:reload4j:1.2.22) with JakartaEeMigrationTransform
Caching disabled for JakartaEeMigrationTransform: .gradle/caches/modules-2/files-2.1/ch.qos.reload4j/reload4j/1.2.22/f9d9e55d1072d7a697d2bf06e1847e93635a7cf9/reload4j-1.2.22.jar because:
  Build cache is disabled
JakartaEeMigrationTransform: .gradle/caches/modules-2/files-2.1/ch.qos.reload4j/reload4j/1.2.22/f9d9e55d1072d7a697d2bf06e1847e93635a7cf9/reload4j-1.2.22.jar is not up-to-date because:
  No history is available.
Performing migration from source [.gradle/caches/modules-2/files-2.1/ch.qos.reload4j/reload4j/1.2.22/f9d9e55d1072d7a697d2bf06e1847e93635a7cf9/reload4j-1.2.22.jar] to destination [/var/folders/gl/9qw1jzv11r5_vxh0ngrk1hcw0000gn/T/jakartaee7252355990471961973transform] with Jakarta EE specification profile [EE]
Migration starting for archive [reload4j-1.2.22.jar] using streaming
Migration finished for archive [reload4j-1.2.22.jar]
Migration completed successfully in [46] milliseconds
Transformed reload4j-1.2.22.jar to JakartaEE reload4j-1.2.22-jakartaee.jar
```

## Applying the Plugin

Refer to the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.netflix.nebula.jakartaee-migration).

## Configuring the Plugin

### Migrate from EE 8 and earlier to EE 9 and later

To enable automatic migration from EE 8 (`javax`) or earlier to EE 9 (`jakarta`) or later, enable all features with `migrate()`:
```
jakartaeeMigration {
    migrate()
}
```

This configures artifact transforms, capabilities and capability resolution for all resolvable configurations in the project. Alternatively, you can apply to configurations conditionally by using: 

```
configurations.all { config ->
  if (config.name != 'myLegacyConfig' && config.isCanBeResolved) {
    jakartaeeMigration.migrate(config)  
  }
}
```

This is most useful for standalone application projects, but could certainly be used for libraries if you control the plugins on it and the downstream projects, applying the migration on all projects.

### Configure Only Capabilities

Configure only capabilities to cause EE API artifacts to conflict if more than one is present. Particularly useful for library authors that want to avoid accidentally exposing more than one implementation, or projects that want to migrate completely to the `jakarta` package namespace. 
```
jakartaeeMigration {
    configureCapabilities()
}
```

### Configure Capabilities Resolution

Configure capabilities resolution for a given configuration. `configureCapabilities()` does not need to be called separately. 
```
jakartaeeMigration {
    resolveCapabilityConflicts('runtimeClasspath') // configuration name or configuration are supported
}
```

### Configure Only Transforms

Enable transforms for a given configuration:
```
jakartaeeMigration {
    transform('runtimeClasspath') // configuration name or configuration are supported
}
```

### Excluding Artifacts from Transformation

Artifacts can be excluded from transforms using `excludeTransform`:
```
jakartaeeMigration {
    // These artifacts have intentional references to 'javax'
    excludeTransform('org.springframework:spring-context')
    excludeTransform('org.springframework:spring-beans')
    excludeTransform('org.apache.tomcat.embed:tomcat-embed-core')
}
```

### Excluding Specification Artifacts from Transformation

Exclude all EE API artifacts from transforms. Ensures that known artifacts providing `javax` classes are not transformed, requiring that replacement `jakarta` artifacts are explicitly added to the project.
```
jakartaeeMigration {
    excludeSpecificationsTransform()
}
```

## Dependency Management

This plugin intentionally avoids managing constraints and other versioning concerns. Use Gradle standard dependency management features in your project configuration.

### Jakarta EE 8

If possible, completely avoid Jakarta EE 8 (i.e. `jakarta` artifact coordinates with `javax` packages). Our goal is this plugin makes the Jakarta EE 9 and later upgrade painless enough that you upgrade straight over that mess. [What is the difference between jaxb-impl and jaxb-runtime?](https://stackoverflow.com/questions/71095913/what-is-the-difference-between-jaxb-impl-and-jaxb-runtime/72151763#72151763) documents one specific option avoiding Jakarta EE 8 API artifacts for JAXB.

### Platforms

Use `platform` to avoid needing to declare first order dependency versions or `enforcedPlatform` ensure a minimum version of a specification is used:
```
    implementation platform('jakarta.platform:jakarta.jakartaee-bom:10.0.0')
    implementation 'jakarta.servlet:jakarta.servlet-api'
```

### Rich Versions / Constraints

For stronger guarantees than platforms, use rich versions and constraints. Note that constraints are published in Gradle Module Metadata, so take care when using them in libraries to avoid unintended side effects. These constraints could be used to successfully use Jakarta EE 8 artifacts and prevent them from accidentally upgrading, but as mentioned above, it's probably more trouble than it's worth.

Use `strictly` to force a version of an artifact:
```
    constraints {
        implementation('jakarta.servlet:jakarta.servlet-api') {
            version {
                strictly '[4.0.0, 5.0.0)'
            }
            because 'JakartaEE 8 required'
        }
    }
```

Or `require` with `reject` to cause dependency resolution to fail if an upgrade is attempted:
```
    constraints {
        implementation('jakarta.servlet:jakarta.servlet-api') {
            version {
                require '4.0.0'
                reject '[5.0.0,)'
            }
            because 'JakartaEE 8 required'
        }
    }
```
