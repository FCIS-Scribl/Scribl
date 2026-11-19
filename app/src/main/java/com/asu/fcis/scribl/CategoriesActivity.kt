
package com.asu.fcis.scribl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.ui.components.NoteCard
import com.asu.fcis.scribl.ui.theme.ScriblTheme
import com.asu.fcis.scribl.utils.toggleArchive
import com.asu.fcis.scribl.utils.togglePin
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.room.withTransaction

class CategoriesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val categoryId = intent.getIntExtra("category_id", -1)
        val database = ScriblDatabase.getDatabase(this)

        setContent {
            ScriblTheme {

                val categories by database.categoryDao()
                    .getAllCategories()
                    .collectAsState(initial = emptyList())

                val notes by database.noteDao()
                    .getNotesByCategory(categoryId)
                    .collectAsState(initial = emptyList())

                val scope = rememberCoroutineScope()
                var showDeleteDialog by remember {
                    mutableStateOf(false)
                }

                val category = categories.find {
                    it.id == categoryId
                }

                val activeNotes = notes
                    .filter { !it.is_archived }
                    .sortedByDescending { it.is_pinned }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    TextButton(onClick = { finish() }) {
                        Text("← Back")
                    }

                    Text(
                        text = category?.name ?: "Category",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Notes: ${activeNotes.size}",
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    if (activeNotes.isEmpty()) {

                        Text("No notes in this category yet.")

                    } else {

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            items(activeNotes) { note ->

                                NoteCard(
                                    note = note,

                                    onNoteClick = {
                                        val intent = Intent(
                                            this@CategoriesActivity,
                                            NoteDetailsActivity::class.java
                                        )

                                        intent.putExtra("note_id", note.id)
                                        startActivity(intent)
                                    },

                                    onPinClick = {
                                        scope.launch {
                                            togglePin(database, note)
                                        }
                                    },

                                    onArchiveClick = {
                                        scope.launch {
                                            toggleArchive(database, note)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            showDeleteDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text(
                            text = "Delete Category",
                            color = Color.White
                        )
                    }
                }
                if (showDeleteDialog) {

                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },

                        title = {
                            Text("Delete Category?")
                        },

                        text = {
                            Text(
                                "Are you sure you want to delete \"${category?.name}\"?\n\n" +
                                        "All notes inside this category will also be permanently deleted."
                            )
                        },

                        confirmButton = {
                            TextButton(
                                onClick = {

                                    scope.launch {

                                        if (category != null) {

                                            database.withTransaction {

                                                // Delete all notes in this category
                                                database.noteDao()
                                                    .deleteNotesByCategory(categoryId)

                                                // Delete the category
                                                database.categoryDao()
                                                    .deleteCategory(category)
                                            }
                                        }

                                        showDeleteDialog = false
                                        finish()
                                    }
                                }
                            ) {
                                Text(
                                    "Delete",
                                    color = Color(0xFFD32F2F)
                                )
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
}
