package com.netflix.gradle.jakartaee.artifacts

import java.io.Serializable

data class ArtifactCoordinate(
    val group: String,
    val name: String
) : Serializable {
    val notation = "${group}:${name}"

    override fun toString() = notation

    fun withVersion(version: String) = ArtifactVersionCoordinate(this, ArtifactVersion(version))

    fun withVersion(version: ArtifactVersion) = ArtifactVersionCoordinate(this, version)
}
