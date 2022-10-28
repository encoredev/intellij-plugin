package dev.encore.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.text.SemVer
import dev.encore.intellij.sqldb.Detector
import dev.encore.intellij.utils.isInEncoreApp
import dev.encore.intellij.utils.runEncoreCommand

/*
 * Startup Activity allows us to run blocking external calls to the encore daemon to extract information
 * about the project, which we can then store and reference at runtime
 */
class StartupActivity : StartupActivity.Background, StartupActivity.RequiredForSmartMode {
    override fun runActivity(project: Project) {
        // Skip our startup if it's not an Encore app
        if (!isInEncoreApp(project)) {
            return
        }

        Encore.LOG.info("Performing startup activity for Encore...")

        // Verify Encore is installed
        val versionOutput = runEncoreCommand(project, "version") ?: return
        if (!versionOutput.startsWith("encore version ")) {
            return
        }
        Encore.encoreInstalled = true

        // Get the version
        val versionString = versionOutput.trim().removePrefix("encore version ")
        if (versionString.startsWith("v")) {
            Encore.encoreVersion = SemVer.parseFromText(versionString.removePrefix("v"))
        } else {
            // If the version string doesn't start with v then it's a dev build, so default to 999.999.999
            Encore.encoreVersion = SemVer(versionString, 999, 999, 999)
        }
        Encore.LOG.info("Verified that Encore is installed and running version ${Encore.encoreVersion}")

        // Ask encore for the database connection info
        if (Encore.isAtLeast(1, 9, 2)) {
            Encore.LOG.info("Getting database connection information from Encore...")
            Detector.loadDatabaseConnection(project)
        }
    }
}
