package dev.encore.intellij.utils

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.project.rootManager
import com.intellij.psi.PsiDirectory

const val EncoreAppFile = "encore.app"

fun isInEncoreApp(dir: PsiDirectory?): Boolean {
    if (dir == null) {
        return false
    }

    val appFile = dir.findFile(EncoreAppFile)
    if (appFile != null && appFile.isValid) {
        return true
    }

    return isInEncoreApp(dir.parentDirectory)
}

fun isInEncoreApp(module: Module): Boolean {
    for (folder in module.rootManager.contentRoots) {
        val appFile = folder.findChild(EncoreAppFile)
        if (appFile != null && appFile.exists()) {
            return true
        }
    }
    return false
}

fun isInEncoreApp(project: Project): Boolean {
    for (module in project.modules) {
        if (isInEncoreApp(module)) {
            return true
        }
    }
    return false
}