package dev.encore.intellij.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "SettingsState",
    storages = [Storage("EncorePlugin.xml")]
)
class SettingsState : PersistentStateComponent<SettingsState> {
    var enabled: Boolean = true
    var encoreBinary: String = "encore"

    override fun getState(): SettingsState {
        return this
    }

    override fun loadState(state: SettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}

fun settingsState(): SettingsState {
    return ApplicationManager.getApplication().getService(SettingsState::class.java)
}
