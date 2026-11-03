package com.asu.fcis.scribl

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.asu.fcis.scribl.data.CategoryEntity
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.data.ScriblDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class ScriblDatabaseTest {
    private lateinit var db: ScriblDatabase
    private lateinit var categoryDao: com.asu.fcis.scribl.data.CategoryDao
    private lateinit var noteDao: com.asu.fcis.scribl.data.NoteDao

    @Before
    fun createDb() {
        println("--- Setting up in-memory Room database ---")
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ScriblDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoryDao = db.categoryDao()
        noteDao = db.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        println("--- Closing Room database ---")
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeCategoryAndReadInList() = runBlocking {
        println("=== Running writeCategoryAndReadInList ===")
        val category = CategoryEntity(name = "Work")
        println("Inserting category: ${category.name}")
        categoryDao.insertCategory(category)

        val categories = categoryDao.getAllCategories().first()
        println("Retrieved categories count: ${categories.size}, name: ${categories[0].name}")
        assertEquals(1, categories.size)
        assertEquals("Work", categories[0].name)
        println("=== writeCategoryAndReadInList Passed ===")
    }

    @Test
    @Throws(Exception::class)
    fun writeNoteWithCategoryAndRead() = runBlocking {
        println("=== Running writeNoteWithCategoryAndRead ===")
        val category = CategoryEntity(name = "Personal")
        val categoryId = categoryDao.insertCategory(category).toInt()
        println("Inserted category '${category.name}' with ID: $categoryId")

        val now = Date()
        val note = NoteEntity(
            title = "Groceries",
            content = "Milk, Eggs, Bread",
            created_at = now,
            updated_at = now,
            is_pinned = true,
            is_archived = false,
            category_id = categoryId
        )
        println("Inserting note: '${note.title}' (pinned: ${note.is_pinned}, created: ${note.created_at})")
        noteDao.insertNote(note)

        val notes = noteDao.getAllNotes().first()
        println("Retrieved notes count: ${notes.size}")
        println("Note details -> Title: ${notes[0].title}, Content: ${notes[0].content}, Pinned: ${notes[0].is_pinned}, Category ID: ${notes[0].category_id}, Created At: ${notes[0].created_at}")

        assertEquals(1, notes.size)
        assertEquals("Groceries", notes[0].title)
        assertEquals(true, notes[0].is_pinned)
        assertEquals(false, notes[0].is_archived)
        assertEquals(categoryId, notes[0].category_id)
        assertEquals(now.time, notes[0].created_at.time)
        println("=== writeNoteWithCategoryAndRead Passed ===")
    }
}
