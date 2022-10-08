package dev.encore.intellij.runconfig

import com.goide.execution.GoBuildingRunConfiguration
import com.goide.execution.GoRunConfigurationBase
import com.goide.execution.GoRunningState
import com.goide.execution.extension.GoRunConfigurationExtension
import com.goide.execution.testing.GoTestRunConfiguration
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.target.TargetedCommandLineBuilder
import com.intellij.execution.target.value.TargetValue
import com.intellij.openapi.project.rootManager

class EncoreRunConfig : GoRunConfigurationExtension() {
    override fun isApplicableFor(configuration: GoRunConfigurationBase<*>): Boolean {
        if (configuration is GoTestRunConfiguration) {
            return isEncoreApp(configuration)
        }
        return false
    }

    override fun isEnabledFor(
        applicableConfiguration: GoRunConfigurationBase<*>,
        runnerSettings: RunnerSettings?
    ): Boolean {
        if (applicableConfiguration is GoTestRunConfiguration) {
            return isEncoreApp(applicableConfiguration)
        }
        return false
    }

    override fun patchCommandLine(
        configuration: GoRunConfigurationBase<*>,
        runnerSettings: RunnerSettings?,
        cmdLine: TargetedCommandLineBuilder,
        runnerId: String,
        state: GoRunningState<out GoRunConfigurationBase<*>>,
        commandLineType: GoRunningState.CommandLineType
    ) {
        var useEncoreBinary = false
        if (configuration is GoTestRunConfiguration) {
            if (configuration.kind == GoBuildingRunConfiguration.Kind.DIRECTORY) {
                // Directory style tests just run `go test -json [dir]`
                // so we always want to swap to the Encore binary for these
                useEncoreBinary = true
            } else {
                // Otherwise we only want to use the Encore binary for build commands, as
                // GoLand first builds the binary (when we want to use Encore)
                // Then uses `go tools test2json [builtbinary]` - where we want to use the standard binary
                useEncoreBinary = commandLineType == GoRunningState.CommandLineType.BUILD
            }
        }
        if (useEncoreBinary) {
            cmdLine.exePath = TargetValue.fixed("encore")
        }
        super.patchCommandLine(configuration, runnerSettings, cmdLine, runnerId, state, commandLineType)
    }

    private fun isEncoreApp(configuration: GoRunConfigurationBase<*>): Boolean {
        for (folder in configuration.getDefaultModule().rootManager.contentRoots) {
            val appFile = folder.findChild("encore.app")
            if (appFile != null && appFile.exists()) {
                return true
            }
        }
        return false
    }
}
