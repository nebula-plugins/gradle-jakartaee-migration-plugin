package com.netflix.gradle.jakartaee

abstract class AbstractMigrationSpec extends AbstractPluginSpec {
    def setup() {
        buildFile << """
jakartaeeMigration {
    migrate()
}
"""
    }
}
