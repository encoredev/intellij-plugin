package dev.encore.intellij.sqldb

import com.intellij.database.Dbms
import com.intellij.database.autoconfig.DataSourceDetector
import com.intellij.database.dataSource.*
import com.intellij.database.model.DasDataSource
import com.intellij.database.model.ObjectKind
import com.intellij.database.model.ObjectName
import com.intellij.database.util.TreePattern
import com.intellij.database.util.TreePatternNode
import com.intellij.database.util.TreePatternNode.NegativeNaming
import com.intellij.database.util.TreePatternNode.PositiveNaming
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import com.intellij.util.queryParameters
import dev.encore.intellij.Encore
import dev.encore.intellij.utils.isInEncoreApp
import dev.encore.intellij.utils.runEncoreCommand
import java.net.URI

class Detector : DataSourceDetector() {
    override fun collectDataSources(module: Module, builder: Builder, onTheFly: Boolean) {
        if (Encore.encoreInstalled && !isInEncoreApp(module)) {
            return
        }

        // Get connection URI from encore and extract the app name
        val uri = module.project.getUserData(DatabaseConnection) ?: return
        val loginParts = uri.userInfo.split(":")
        val appName = loginParts[0]

        // IntelliJ's database can parse URLs in the following format
        // We use the username / password in the query string here as work around
        // as using the `withUser` / `withPassword` doesn't perminately store the password
        // jdbc:postgresql://[{host::localhost}[:{port::5432}]][/{database:database/[^?]+:postgres}?][\?<&,user={user:param},password={password:param},{:identifier}={:param}>]
        val query = uri.queryParameters.toMutableMap()
        query["user"] = appName

        // Propose the local environment
        query["password"] = "local"
        val localQueryString = query.toList().joinToString(separator = "&") { "${it.first}=${it.second}" }
        builder.reset().withDbms(Dbms.POSTGRES)
            .withGroupName(loginParts[0])
            .withName("Local Environment")
            .withComment("Used by `encore run`")
            .withUrl("jdbc:postgresql://${uri.host}:${uri.port}/postgres?${localQueryString}")
            .withJdbcAdditionalProperty("domain-auth", "user-pass") // This forces persistent storage of the password
            .withCallback(ApplyAdditionalConnectionSettings())
            .commit()

        // Propose the test environment
        query["password"] = "test"
        val testQueryString = query.toList().joinToString(separator = "&") { "${it.first}=${it.second}" }
        builder.reset().withDbms(Dbms.POSTGRES)
            .withGroupName(loginParts[0])
            .withName("Local Test Environment")
            .withComment("Used by `encore test`")
            .withUrl("jdbc:postgresql://${uri.host}:${uri.port}/postgres?${testQueryString}")
            .withJdbcAdditionalProperty("domain-auth", "user-pass") // This forces persistent storage of the password
            .withCallback(ApplyAdditionalConnectionSettings())
            .commit()
    }

    override fun isRelevantFile(file: PsiFile): Boolean {
        return isInEncoreApp(file.containingDirectory)
    }

    companion object {
        val DatabaseConnection = Key<URI?>("encore-db-uri")

        /** Stores this in the use data, to be read by the discovery read only thread later */
        fun loadDatabaseConnection(project: Project): URI? {
            try {
                var connectionURL = runEncoreCommand(project, "db", "conn-uri", "-e", "local", "_any_") ?: return null
                connectionURL = connectionURL.trim()

                project.putUserData(DatabaseConnection, URI(connectionURL))
            } catch (e: Exception) {
                Encore.LOG.error("unable to get connection URI from encore daemon", e)
                project.putUserData(DatabaseConnection, null)
            }

            return null
        }
    }

    private class ApplyAdditionalConnectionSettings : Callback() {
        override fun onCreated(dataSource: DasDataSource) {
            val source = dataSource.localDataSource ?: return

            // Create the default pattern of what databases and schemas
            // we want IntelliJ to index
            val treePattern = TreePattern(
                TreePatternNode.Group(
                    ObjectKind.DATABASE,
                    null, // All positive names allowed
                    TreePatternNode(
                        NegativeNaming(
                            ObjectName("postgres", false), // Don't show the default postgres database

                            // IntelliJ bug where on first load it shows these two and then complains  that they
                            // don't exist
                            ObjectName("template0", false),
                            ObjectName("template1", false),
                        ),
                        arrayOf(
                            TreePatternNode.Group(
                                ObjectKind.SCHEMA,
                                arrayOf(
                                    // We only want to pickup public schema's
                                    TreePatternNode(
                                        PositiveNaming(ObjectName("public", false)),
                                        arrayOf(),
                                    )
                                ),
                                null
                            )
                        ),
                    ),
                )
            )
            source.schemaMapping.introspectionScope = treePattern
        }
    }
}
