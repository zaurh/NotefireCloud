package com.zaurh.notefireremote.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val color: NoteColor = NoteColor.Default,
    val isPinned: Boolean = false,
    /** Epoch millis of the last edit, or null if unknown. */
    val updatedAt: Long? = null,
) {
    val isBlank: Boolean get() = title.isBlank() && content.isBlank()

    fun matches(query: String): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true
        return title.contains(q, ignoreCase = true) || content.contains(q, ignoreCase = true)
    }
}

/** Persisted by [name], so never rename entries — only add new ones. */
enum class NoteColor {
    Default, Red, Orange, Yellow, Green, Teal, Blue, Purple, Pink;

    companion object {
        fun fromName(name: String?): NoteColor = entries.firstOrNull { it.name == name } ?: Default
    }
}
