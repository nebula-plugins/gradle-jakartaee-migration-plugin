package com.netflix.gradle.jakartaee

import org.gradle.api.Plugin
import org.gradle.api.Project

public class JakartaEeMigrationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            "jakartaeeMigration",
            JakartaEeMigrationExtension::class.java,
            project.configurations,
            project.dependencies
        )
        extension.registerTransform()
    }
}
