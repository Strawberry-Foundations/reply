package org.strawberryfoundations.reply.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "2.1.2",
            date = "Apr 23, 2026",
            changes = listOf(
                "[NEW] Added translations for debug view",
                "[NEW] Readded icon in TopAppBar",
                "[PRJ] Updated AGP to v9.2.0",
                "[PRJ] Dependency updates",
            )
        ),
        ChangelogEntry(
            version = "2.1.1",
            date = "Apr 7, 2026",
            changes = listOf(
                "[PRJ] Gradle build config update to match Google's AGP 9.x version",
            )
        ),
        ChangelogEntry(
            version = "2.1.0",
            date = "Apr 3, 2026",
            changes = listOf(
                "[NEW] New app logo",
                "[NEW] Current title from NavigationBar will now display in the CenterAlignedTopAppBar",
                "[UI] Adjusted icon size in the settings view",
                "[BUG] Fix wrong translation in active exercise view",
                "[PRJ] Updated AGP to v9.1.0",
                "[PRJ] Dependency updates",
            )
        ),
        ChangelogEntry(
            version = "2.0.1",
            date = "Feb 7, 2026",
            changes = listOf(
                "[NEW] Added session count to device sync page",
                "[UX] Improved haptic feedback",
                "[BUG] Fixed Backup import throwing import error",
                "[BUG] Fixed wrong font in DebugView",
            )
        ),
        ChangelogEntry(
            version = "2.0.0",
            date = "Feb 4, 2026",
            changes = listOf(
                "[NEW] New exercise view with detailed info",
                "[NEW] Added active exercise tracking to track your progress",
                "[UX] Improved syncing with wearable",
                "[UI] Redesigned user interface for better usability",
                "[UX] Added haptic feedback",
                "[FIX] Fixed using outdated fonts in some dialogs",
                "[PRJ] New branding",
                "[PRJ] Major code base refactoring",
                "[PRJ] For consistency and maintaining the app, both mobile and wearable app are using almost the same codebase",
                "[BUG] Bug fixes and performance improvements",
            )
        ),
    )
}
