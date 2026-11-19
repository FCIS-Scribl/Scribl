package com.asu.fcis.scribl.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): NoteEntity?

    @Query("SELECT * FROM notes WHERE category_id = :categoryId")
    fun getNotesByCategory(categoryId: Int): Flow<List<NoteEntity>>
    @Query("""
    SELECT * FROM notes
    WHERE title LIKE '%' || :searchQuery || '%'
       OR content LIKE '%' || :searchQuery || '%'
    ORDER BY is_pinned DESC, updated_at DESC
""")
    fun searchNotes(searchQuery: String): Flow<List<NoteEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE category_id = :categoryId")
    suspend fun deleteNotesByCategory(categoryId: Int)
}
