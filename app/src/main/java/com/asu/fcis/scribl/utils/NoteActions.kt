
package com.asu.fcis.scribl.utils

import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.data.ScriblDatabase

suspend fun togglePin(
    database: ScriblDatabase,
    note: NoteEntity
) {
    val updatedNote = note.copy(
        is_pinned = !note.is_pinned
    )

    database.noteDao().updateNote(updatedNote)
}
suspend fun toggleArchive(
    database: ScriblDatabase,
    note: NoteEntity
) {
    val updatedNote = note.copy(
        is_archived = !note.is_archived
    )

    database.noteDao().updateNote(updatedNote)
}