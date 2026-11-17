
package com.asu.fcis.scribl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.ui.theme.ScriblTheme
import kotlinx.coroutines.launch
import java.util.Date

class NewNoteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ScriblDatabase.getDatabase(this)

        setContent {
            ScriblTheme {

                var title by remember { mutableStateOf("") }
                var content by remember { mutableStateOf("") }

                val categories by database.categoryDao()
                    .getAllCategories()
                    .collectAsState(initial = emptyList())

                var selectedCategoryId by remember {
                    mutableStateOf<Int?>(null)
                }

                var expanded by remember {
                    mutableStateOf(false)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "New Note",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Note Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Box {
                        OutlinedButton(
                            onClick = { expanded = true }
                        ) {
                            Text(
                                categories.find {
                                    it.id == selectedCategoryId
                                }?.name ?: "Select Category"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() &&
                                content.isNotBlank()
                            ) {
                                val now = Date()

                                val note = NoteEntity(
                                    title = title.trim(),
                                    content = content.trim(),
                                    created_at = now,
                                    updated_at = now,
                                    category_id = selectedCategoryId
                                )

                                lifecycleScope.launch {
                                    database.noteDao().insertNote(note)
                                    finish()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Note")
                    }
                }
            }
        }
    }
}
