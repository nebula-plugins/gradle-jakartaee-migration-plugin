package com.netflix.gradle.jakartaee.artifacts

import java.io.Serializable

internal data class ArtifactVersionCoordinate(val module: ArtifactCoordinate, val version: ArtifactVersion): Serializable {
    val notation: String = "${module.group}:${module.name}:${version}"

    override fun toString(): String = notation
}
