package dev.encore.intellij.utils

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.project.modules
import com.intellij.openapi.project.rootManager
import com.intellij.psi.PsiDirectory
import dev.encore.intellij.settings.settingsState

const val EncoreAppFile = "encore.app"

fun isInEncoreApp(dir: PsiDirectory?): Boolean {
    if (!settingsState().enabled) {
        return false
    }

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
    if (!settingsState().enabled) {
        return false
    }

    for (folder in module.rootManager.contentRoots) {
        val appFile = folder.findChild(EncoreAppFile)
        if (appFile != null && appFile.exists()) {
            return true
        }
    }
    return false
}

fun isInEncoreApp(project: Project): Boolean {
    if (!settingsState().enabled) {
        return false
    }

    val projectDir = project.guessProjectDir() ?: return false
    val appFile = projectDir.findChild(EncoreAppFile)
    if (appFile != null && appFile.exists()) {
        return true
    }
    return false
}
