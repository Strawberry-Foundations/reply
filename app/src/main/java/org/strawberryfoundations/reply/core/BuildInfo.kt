package org.strawberryfoundations.reply.core

import android.content.Context
import android.content.pm.PackageManager
import kotlin.toString


fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (_: PackageManager.NameNotFoundException) {
        "N/A"
    }.toString()
}
