package com.chekurda.gradle

import com.chekurda.gradle.root_config.ApplicationCfgPlugin
import com.chekurda.gradle.sub_config.ModuleCfgPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.function.Function

/**
 * Плагин производит установку типовых плагинов для проекта.
 */
class GradleCfgPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.ext.gradleConfig = { Function<GradleConfig, Void> config ->
            config.apply(new GradleConfig(project))
        }
    }

    static class GradleConfig {
        private def project

        private GradleConfig(Project project) {
            this.project = project
        }

        void enableApplicationCfg() {
            project.getPlugins().apply(ApplicationCfgPlugin.class)
        }

        void enableModuleCfg() {
            project.getPlugins().apply(ModuleCfgPlugin.class)
        }
    }
}