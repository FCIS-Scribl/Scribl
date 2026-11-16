package com.asu.fcis.scribl

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.asu.fcis.scribl.data.CategoryEntity
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.ui.theme.ScriblTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
                val workId = categoryDao.insertCategory(CategoryEntity(name = "Work")).toInt()
                val personalId = categoryDao.insertCategory(CategoryEntity(name = "Personal")).toInt()
                val ideasId = categoryDao.insertCategory(CategoryEntity(name = "Ideas")).toInt()

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
        setContentView(R.layout.main_view)

        val label = findViewById<TextView>(R.id.textView)

        lifecycleScope.launch {
            val notes = database.noteDao().getAllNotes().first()
            if (notes.isNotEmpty()) {
                label.text = notes[0].content
            } else {
                label.text = getString(R.string.no_notes_found)
            }
        }
    }
}
