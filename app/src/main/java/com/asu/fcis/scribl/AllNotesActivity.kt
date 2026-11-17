
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
import kotlinx.coroutines.launch
import com.asu.fcis.scribl.utils.togglePin
import com.asu.fcis.scribl.utils.toggleArchive

class AllNotesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ScriblDatabase.getDatabase(this)

        setContent {
            ScriblTheme {

                val notes by database.noteDao()
                    .getAllNotes()
                    .collectAsState(initial = emptyList())

                val scope = rememberCoroutineScope()

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
                        text = "All Notes",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Total Notes: ${activeNotes.size}",
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    if (activeNotes.isEmpty()) {

                        Text("No notes available.")

                    } else {

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            items(activeNotes) { note ->

                                NoteCard(
                                    note = note,

                                    onNoteClick = {
                                        val intent = Intent(
                                            this@AllNotesActivity,
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
                }
            }
        }
    }
}
