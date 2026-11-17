
package com.asu.fcis.scribl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.asu.fcis.scribl.data.CategoryEntity
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.ui.theme.ScriblTheme
import com.asu.fcis.scribl.ui.screens.HomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add initial data to the database
        val database = ScriblDatabase.getDatabase(applicationContext)

        lifecycleScope.launch(Dispatchers.IO) {
            val noteDao = database.noteDao()
            val categoryDao = database.categoryDao()

            if (noteDao.getAllNotes().first().isEmpty()) {

                val workId = categoryDao.insertCategory(
                    CategoryEntity(name = "Work")
                ).toInt()

                val personalId = categoryDao.insertCategory(
                    CategoryEntity(name = "Personal")
                ).toInt()

                val ideasId = categoryDao.insertCategory(
                    CategoryEntity(name = "Ideas")
                ).toInt()

                val now = Date()

                noteDao.insertNote(
                    NoteEntity(
                        title = "Finish Database Implementation",
                        content = "[x] Implement Room entities, DAOs, and database queries for Scribl.",
                        created_at = now,
                        updated_at = now,
                        is_pinned = true,
                        is_archived = false,
                        category_id = workId
                    )
                )

                noteDao.insertNote(
                    NoteEntity(
                        title = "Grocery Shopping List",
                        content = "Buy milk, eggs, bread, and coffee beans.",
                        created_at = now,
                        updated_at = now,
                        is_pinned = false,
                        is_archived = false,
                        category_id = personalId
                    )
                )

                noteDao.insertNote(
                    NoteEntity(
                        title = "App Feature Idea",
                        content = "Add dark mode toggle and tag filtering to Scribl.",
                        created_at = now,
                        updated_at = now,
                        is_pinned = false,
                        is_archived = false,
                        category_id = ideasId
                    )
                )
            }
        }

        enableEdgeToEdge()

        setContent {
            ScriblTheme {
                HomeScreen(

                    // Add Note
                    onAddNoteClick = {
                        val intent = Intent(
                            this,
                            NewNoteActivity::class.java
                        )
                        startActivity(intent)
                    },

                    // Category Click
                    onCategoryClick = { categoryId ->
                        val intent = Intent(
                            this,
                            CategoriesActivity::class.java
                        )

                        intent.putExtra("category_id", categoryId)
                        startActivity(intent)
                    },

                    // All Notes
                    onAllNotesClick = {
                        val intent = Intent(
                            this,
                            AllNotesActivity::class.java
                        )
                        startActivity(intent)
                    },
                    // Archive
                    onArchiveClick = {
                        val intent = Intent(
                            this,
                            ArchiveActivity::class.java
                        )
                        startActivity(intent)
                    }



                )
            }
        }
    }
}
