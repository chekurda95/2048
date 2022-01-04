package com.chekurda.gradle.root_config

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Плагин производит установку параметров для приложения.
 */
class ApplicationCfgPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: 'com.android.application'

        project.android {
            defaultConfig {
                applicationId "${project.rootProject.ext.applicationId}"
            }
            dexOptions {
                javaMaxHeapSize "10g"
                preDexLibraries true
            }
        }
    }
}