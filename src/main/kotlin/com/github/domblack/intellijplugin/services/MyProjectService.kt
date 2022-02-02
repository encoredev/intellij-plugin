package com.github.domblack.intellijplugin.services

import com.intellij.openapi.project.Project
import com.github.domblack.intellijplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
