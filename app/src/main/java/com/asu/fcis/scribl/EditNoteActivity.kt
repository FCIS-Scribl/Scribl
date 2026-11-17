
package com.asu.fcis.scribl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.ui.theme.ScriblTheme
import kotlinx.coroutines.launch
import java.util.Date

class EditNoteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ScriblDatabase.getDatabase(this)
        val noteId = intent.getIntExtra("note_id", -1)

        setContent {
            ScriblTheme {

                var title by remember { mutableStateOf("") }
                var content by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf("") }

                var currentNote by remember {
                    mutableStateOf<NoteEntity?>(null)
                }

                val scope = rememberCoroutineScope()

                LaunchedEffect(noteId) {

                    if (noteId != -1) {
                        currentNote =
                            database.noteDao().getNoteById(noteId)

                        currentNote?.let { note ->
                            title = note.title
                            content = note.content
                        }
                    }

                    isLoading = false
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    TextButton(
                        onClick = { finish() }
                    ) {
                        Text("← Back")
                    }

                    Text(
                        text = "Edit Note",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading) {

                        CircularProgressIndicator()

                    } else if (currentNote == null) {

                        Text("Note not found.")

                    } else {

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            minLines = 5
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {

                                if (title.isBlank()) {
                                    errorMessage = "Title cannot be empty."
                                    return@Button
                                }

                                scope.launch {

                                    val noteToUpdate =
                                        currentNote ?: return@launch

                                    val updatedNote = noteToUpdate.copy(
                                        title = title.trim(),
                                        content = content.trim(),
                                        updated_at = Date()
                                    )

                                    database.noteDao()
                                        .updateNote(updatedNote)

                                    finish()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}
