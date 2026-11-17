
package com.asu.fcis.scribl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.data.NoteEntity
import com.asu.fcis.scribl.ui.theme.*
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

                val categoryName = categories
                    .find { it.id == note?.category_id }
                    ?.name ?: "No Category"

                val secondaryText = Color(0xFF78838D)
                val cardBackground = Color.White
                val deleteColor = Color(0xFFB94F4F)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {

                    // Back Button
                    TextButton(
                        onClick = { finish() },
                        contentPadding = PaddingValues(
                            horizontal = 0.dp,
                            vertical = 4.dp
                        )
                    ) {
                        Text(
                            text = "← Back",
                            color = DarkBlue,
                            fontSize = 16.sp
                        )
                    }

                    if (note == null) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 70.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Teal
                            )
                        }

                    } else {

                        // Note Header
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Text(
                                text = "NOTE DETAILS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Teal,
                                letterSpacing = 2.sp
                            )

                            Text(
                                text = note.title,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                lineHeight = 35.sp
                            )

                            // Note Status
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Teal.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = categoryName,
                                        color = Teal,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 7.dp
                                        )
                                    )
                                }

                                if (note.is_pinned) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFFFFF1D6)
                                    ) {
                                        Text(
                                            text = "Pinned",
                                            color = Color(0xFF9A6B19),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 7.dp
                                            )
                                        )
                                    }
                                }

                                if (note.is_archived) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFFE8EAF0)
                                    ) {
                                        Text(
                                            text = "Archived",
                                            color = DarkBlue,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 7.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Note Content Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackground
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Text(
                                    text = "Note Content",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlue
                                )

                                HorizontalDivider(
                                    color = LightGray
                                )

                                Text(
                                    text = note.content.ifBlank {
                                        "No content"
                                    },
                                    fontSize = 16.sp,
                                    color = DarkText,
                                    lineHeight = 26.sp
                                )
                            }
                        }

                        // Note Information Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackground
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 1.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Text(
                                    text = "Note Information",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkBlue
                                )

                                // Category
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "▣",
                                        color = Teal,
                                        fontSize = 22.sp
                                    )

                                    Column {
                                        Text(
                                            text = "Category",
                                            fontSize = 12.sp,
                                            color = secondaryText
                                        )

                                        Text(
                                            text = categoryName,
                                            fontSize = 15.sp,
                                            color = DarkText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                HorizontalDivider(color = LightGray)

                                // Created Date
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "◷",
                                        color = Teal,
                                        fontSize = 23.sp
                                    )

                                    Column {
                                        Text(
                                            text = "Created",
                                            fontSize = 12.sp,
                                            color = secondaryText
                                        )

                                        Text(
                                            text = dateFormat.format(note.created_at),
                                            fontSize = 15.sp,
                                            color = DarkText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                HorizontalDivider(color = LightGray)

                                // Last Modified
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "↻",
                                        color = Teal,
                                        fontSize = 23.sp
                                    )

                                    Column {
                                        Text(
                                            text = "Last Modified",
                                            fontSize = 12.sp,
                                            color = secondaryText
                                        )

                                        Text(
                                            text = dateFormat.format(note.updated_at),
                                            fontSize = 15.sp,
                                            color = DarkText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Edit Button
                        Button(
                            onClick = {
                                val editIntent = Intent(
                                    this@NoteDetailsActivity,
                                    EditNoteActivity::class.java
                                )

                                editIntent.putExtra("note_id", note.id)
                                startActivity(editIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Teal
                            )
                        ) {
                            Text(
                                text = "✎  Edit Note",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        // Delete Button
                        OutlinedButton(
                            onClick = {
                                showDeleteDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                1.dp,
                                deleteColor.copy(alpha = 0.6f)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = deleteColor
                            )
                        ) {
                            Text(
                                text = "Delete Note",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                // Delete Confirmation Dialog
                if (showDeleteDialog && note != null) {

                    AlertDialog(
                        onDismissRequest = {
                            showDeleteDialog = false
                        },
                        shape = RoundedCornerShape(20.dp),
                        title = {
                            Text(
                                text = "Delete Note?",
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        },
                        text = {
                            Text(
                                text = "Are you sure you want to delete this note? This action cannot be undone."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val noteToDelete = note

                                    scope.launch {
                                        database.noteDao()
                                            .deleteNote(noteToDelete)

                                        finish()
                                    }
                                }
                            ) {
                                Text(
                                    text = "Delete",
                                    color = deleteColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Cancel", color = DarkBlue)
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
