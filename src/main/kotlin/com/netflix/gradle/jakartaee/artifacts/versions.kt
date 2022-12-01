package com.netflix.gradle.jakartaee.artifacts

val ArtifactVersion.minorVersion: ArtifactVersion
    get() = if (parts.size > 1) ArtifactVersion("${parts[0]}.${parts[1]}") else ArtifactVersion(parts[0])
