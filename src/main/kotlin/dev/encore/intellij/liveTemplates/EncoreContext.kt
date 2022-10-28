package dev.encore.intellij.liveTemplates

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import dev.encore.intellij.utils.isInEncoreApp

class EncoreContext : TemplateContextType("ENCORE_GO_FILE", "Encore Go File") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return isInEncoreApp(templateActionContext.file.originalFile.containingDirectory) && templateActionContext.file.name.endsWith(".go")
    }
}
