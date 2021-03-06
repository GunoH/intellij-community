// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.kotlin.tools.projectWizard.plugins

import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.kotlin.tools.projectWizard.core.Context
import org.jetbrains.kotlin.tools.projectWizard.plugins.buildSystem.JpsPlugin
import org.jetbrains.kotlin.tools.projectWizard.plugins.buildSystem.MavenPlugin
import org.jetbrains.kotlin.tools.projectWizard.plugins.buildSystem.gradle.GroovyDslPlugin
import org.jetbrains.kotlin.tools.projectWizard.plugins.buildSystem.gradle.KotlinDslPlugin
import org.jetbrains.kotlin.tools.projectWizard.plugins.kotlin.KotlinPlugin
import org.jetbrains.kotlin.tools.projectWizard.plugins.projectTemplates.ProjectTemplatesPlugin
import org.jetbrains.kotlin.tools.projectWizard.plugins.templates.*

object Plugins {
    val allPlugins = { context: Context ->
        persistentListOf(
            StructurePlugin(context),

            GroovyDslPlugin(context),
            KotlinDslPlugin(context),
            JpsPlugin(context),
            MavenPlugin(context),

            KotlinPlugin(context),
            TemplatesPlugin(context),
            ProjectTemplatesPlugin(context),
            RunConfigurationsPlugin(context),
            AndroidPlugin(context),

            // templates
            ConsoleJvmApplicationTemplatePlugin(context),
            KtorTemplatesPlugin(context),
            JsTemplatesPlugin(context),
            ReactJsTemplatesPlugin(context),
            SimpleNodeJsTemplatesPlugin(context),
            NativeConsoleApplicationTemplatePlugin(context),
            ComposeTemplatesPlugin(context),
            MobileMppTemplatePlugin(context),
        )
    }
}