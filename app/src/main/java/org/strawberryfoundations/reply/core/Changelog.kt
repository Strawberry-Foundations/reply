package org.strawberryfoundations.reply.core

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

object Changelog {
    val entries = listOf(
        ChangelogEntry(
            version = "2.0.0",
            date = "February 2026",
            changes = listOf(
                "New exercise view with detailed info",
                "Added active exercise tracking to track your progress",
                "Improved syncing with wearable",
                "Redesigned user interface for better usability",
                "New branding",
                "Bug fixes and performance improvements",
            )
        ),
    )
}
