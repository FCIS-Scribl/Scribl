
package com.asu.fcis.scribl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.ui.theme.ScriblTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class NoteDetailsActivity : ComponentActivity() {

    private lateinit var database: ScriblDatabase
    private var noteId: Int = -1

    private var noteState = mutableStateOf<NoteEntity?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteId = intent.getIntExtra("note_id", -1)
        database = ScriblDatabase.getDatabase(this)

        setContent {
            ScriblTheme {

                val note = noteState.value

                var showDeleteDialog by remember {
                    mutableStateOf(false)
                }

                val categories by database.categoryDao()
                    .getAllCategories()
                    .collectAsState(initial = emptyList())

                val scope = rememberCoroutineScope()

                val dateFormat = SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    TextButton(
                        onClick = { finish() }
                    ) {
                        Text("← Back")
                    }

                    if (note == null) {

                        Text("Loading note...")

                    } else {

                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        HorizontalDivider()

                        val categoryName = categories.find {
                            it.id == note.category_id
                        }?.name ?: "No Category"

                        Text("Category: $categoryName")

                        Text(
                            "Created: ${dateFormat.format(note.created_at)}"
                        )

                        Text(
                            "Last Modified: ${dateFormat.format(note.updated_at)}"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val editIntent = Intent(
                                    this@NoteDetailsActivity,
                                    EditNoteActivity::class.java
                                )

                                editIntent.putExtra("note_id", note.id)
                                startActivity(editIntent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Edit Note")
                        }

                        OutlinedButton(
                            onClick = {
                                showDeleteDialog = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete Note")
                        }
                    }
                }

                if (showDeleteDialog) {

                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },
                        title = {
                            Text("Delete Note")
                        },
                        text = {
                            Text("Are you sure you want to delete this note?")
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val noteToDelete = note

                                    if (noteToDelete != null) {
                                        scope.launch {
                                            database.noteDao()
                                                .deleteNote(noteToDelete)

                                            finish()
                                        }
                                    }
                                }
                            ) {
                                Text("Delete")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (noteId != -1) {
            lifecycleScope.launch {
                noteState.value =
                    database.noteDao().getNoteById(noteId)
            }
        }
    }
}
