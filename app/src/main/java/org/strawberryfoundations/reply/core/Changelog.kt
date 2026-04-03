package org.strawberryfoundations.reply.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "2.0.2",
            date = "Apr 3, 2026",
            changes = listOf(
                "Updated AGP to v9.1.0",
                "Dependency updates",
                "New app logo",
                "Current title from NavigationBar will now display in the CenterAlignedTopAppBar",
                "Adjusted icon size in the settings view"
            )
        ),
        ChangelogEntry(
            version = "2.0.1",
            date = "Feb 7, 2026",
            changes = listOf(
                "Improved haptic feedback",
                "Fixed Backup import throwing import error",
                "Fixed wrong font in DebugView",
                "Added session count to device sync page",
            )
        ),
        ChangelogEntry(
            version = "2.0.0",
            date = "Feb 4, 2026",
            changes = listOf(
                "New branding",
                "New exercise view with detailed info",
                "Added active exercise tracking to track your progress",
                "Improved syncing with wearable",
                "Redesigned user interface for better usability",
                "Fixed using outdated fonts in some dialogs",
                "Added haptic feedback",
                "Major code base refactoring",
                "For consistency and maintaining the app, both mobile and wearable app are using almost the same codebase",
                "Bug fixes and performance improvements",
            )
        ),
    )
}
