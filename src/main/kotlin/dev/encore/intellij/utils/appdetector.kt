package dev.encore.intellij.utils

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
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

fun isInEncoreApp(project: Project): Boolean {
    if (!settingsState().enabled) {
        return false
    }

    // Check if the projects root directory has an encore.app file
    val projectDir = project.guessProjectDir() ?: return false
    val appFile = projectDir.findChild(EncoreAppFile)
    if (appFile != null && appFile.exists()) {
        return true
    }

    // Then check the projects modules
    return ModuleManager.getInstance(project).modules.any { module ->
        module.rootManager.contentRoots.any { folder ->
            folder.findChild(EncoreAppFile)?.exists() == true
        }
    }
}
