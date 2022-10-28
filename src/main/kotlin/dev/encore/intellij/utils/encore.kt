package dev.encore.intellij.utils

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ScriptRunnerUtil
import com.intellij.openapi.project.Project
import dev.encore.intellij.Encore
import dev.encore.intellij.settings.settingsState


/**
 * This command runs the given command against Encore within the projects base path
 *
 * If the command takes more than 30 seconds to complete or reports an error, null is returned
 *
 * This must be called from background threads, and cannot be called from Read threads - otherwise
 * IntelliJ will throw an exception
 */
fun runEncoreCommand(project: Project, vararg argsToEncore: String): String? {
    val cmdLine = GeneralCommandLine()
    cmdLine.setWorkDirectory(project.basePath)
    cmdLine.exePath = settingsState().encoreBinary
    cmdLine.addParameters(argsToEncore.toMutableList())

    return try {
        ScriptRunnerUtil.getProcessOutput(cmdLine, ScriptRunnerUtil.STDOUT_OUTPUT_KEY_FILTER, 30000)
    } catch (e: ExecutionException) {
        Encore.LOG.error("Unable to execute command: ${argsToEncore.joinToString(" ")}", e)
        null
    }
}
