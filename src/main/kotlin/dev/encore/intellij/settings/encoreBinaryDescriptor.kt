package dev.encore.intellij.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.TimeUnit

class encoreBinaryDescriptor: FileChooserDescriptor(true, false, false, false, false, false) {
    override fun validateSelectedFiles(files: Array<out VirtualFile>) {
        if (files.isEmpty()) {
            throw Exception("You must select an path to the encore binary")
        }

        if (files.size > 1) {
            throw Exception("You must only pick one binary")
        }

        if (files[0].isDirectory) {
            throw Exception("Cannot be passed a directory")
        }

        val command = "${files[0].path} version"
        val parts = command.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(5, TimeUnit.SECONDS)
        if (proc.exitValue() != 0) {
            val error = proc.errorStream.bufferedReader().readText()
            throw Exception("When querying for the Encore version, got an error:\n\n${error}")
        }
        proc.destroy()
        val versionString = proc.inputStream.bufferedReader().readText()
        if (!versionString.trim().startsWith("encore version ")) {
            throw Exception("Binary did not report `encore version ` as a response to a request for version parameters")
        }
    }
}
