package com.netflix.gradle.jakartaee.artifacts

enum class ArtifactType {
    API,

    /**
     * Artifact bundles both API and implementation, and should be preferred.
     */
    BUNDLE
}