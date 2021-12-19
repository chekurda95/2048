package com.chekurda.gradle.sub_config

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Плагин производит установку базовых параметров для модулей:
 * compileSdkVersion, buildToolsVersion, targetSdkVersion, minSdkVersion, versionName и versionCode.
 */
class ModuleCfgPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.android {
            compileSdkVersion project.rootProject.ext.compileSdkVersion
            buildToolsVersion project.rootProject.ext.buildToolsVersion
            defaultConfig {
                minSdkVersion project.rootProject.ext.minSdkVersion
                targetSdkVersion project.rootProject.ext.targetSdkVersion
                versionCode project.rootProject.ext.versionCode
                versionName project.rootProject.ext.versionName
            }

            compileOptions {
                def compatibility = project.rootProject.ext.javaVersion
                sourceCompatibility compatibility
                targetCompatibility compatibility
            }
            def pluginContainer = project.getPlugins()

            if (pluginContainer.hasPlugin('kotlin-android')) {
                kotlinOptions {
                    jvmTarget = project.rootProject.ext.javaVersion
                }
            }
            buildTypes {
                release {
                    minifyEnabled false
                    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
                }
            }
            lintOptions {
                abortOnError false
            }
        }
    }
}
