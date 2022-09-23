package dev.encore.intellij.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiDirectory

class EncoreContext : TemplateContextType("ENCORE_GO_FILE", "Encore Go File") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return isEncoreApp(templateActionContext.file.originalFile.containingDirectory) && templateActionContext.file.name.endsWith(".go")
    }

    private fun isEncoreApp(dir: PsiDirectory?): Boolean {
        if (dir == null) {
            return false
        }

        val appFile = dir.findFile("encore.app")
        if (appFile != null && appFile.isValid) {
            return true
        }

        return isEncoreApp(dir.parentDirectory)
    }
}
