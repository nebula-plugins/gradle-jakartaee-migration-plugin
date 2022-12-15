package com.netflix.gradle.jakartaee.artifacts

import org.gradle.api.artifacts.component.ModuleComponentIdentifier

// We don't implement these interfaces ourselves because equals/hashCode for Gradle's internal implementations don't
// consider other classes that might satisfy the interface, so you can wind up with weird results if you try to pass
// in your own implementation.
internal fun ModuleComponentIdentifier.toArtifactCoordinate() = ArtifactCoordinate(group, module).withVersion(version)

internal val ArtifactVersion.minorVersion: ArtifactVersion
    get() = if (parts.size > 1) ArtifactVersion("${parts[0]}.${parts[1]}") else ArtifactVersion(parts[0])
