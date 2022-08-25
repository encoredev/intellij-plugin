package dev.encore.intellij.appfile

import com.intellij.json.json5.Json5Language
import com.intellij.openapi.fileTypes.LanguageFileType
import dev.encore.intellij.Icons
import javax.swing.Icon

class EncoreAppFileType : LanguageFileType(Json5Language.INSTANCE) {
    companion object {
        val INSTANCE = EncoreAppFileType()
    }

    override fun getName(): String {
        return "Encore App Manifest"
    }

    override fun getDescription(): String {
        return "A file which describes the overall Encore Application"
    }

    override fun getDefaultExtension(): String {
        return "app"
    }

    override fun getIcon(): Icon {
        return Icons.Icon
    }
}
