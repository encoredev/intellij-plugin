package dev.encore.intellij

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.text.SemVer

object Encore {
    val LOG: Logger = Logger.getInstance(Encore.javaClass)

    /* Is encore installed? */
    @Volatile
    var encoreInstalled = false

    /* What version is Encore? */
    @Volatile
    var encoreVersion: SemVer? = null

    /*
    Returns true if Encore is at least at the given release
     */
    fun isAtLeast(major: Int, minor: Int, patch: Int): Boolean {
        val version = encoreVersion ?: return false

        return version.isGreaterOrEqualThan(major, minor, patch)
    }
}
