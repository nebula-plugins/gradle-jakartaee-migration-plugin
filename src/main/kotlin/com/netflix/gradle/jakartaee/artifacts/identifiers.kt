package com.netflix.gradle.jakartaee.artifacts

import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier

// We don't implement these interfaces ourselves because equals/hashCode for Gradle's internal implementations don't
// consider other classes that might satisfy the interface, so you can wind up with weird results if you try to pass
// in your own implementation.

fun ModuleIdentifier.toArtifactCoordinate() = ArtifactCoordinate(group, name)
fun ModuleVersionIdentifier.toArtifactVersionCoordinate() = ArtifactCoordinate(group, name).withVersion(version)
fun ModuleComponentIdentifier.toArtifactCoordinate() = ArtifactCoordinate(group, module).withVersion(version)
