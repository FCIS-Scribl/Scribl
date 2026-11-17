
package com.asu.fcis.scribl.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asu.fcis.scribl.data.NoteEntity

@Composable
fun NoteCard(
    note: NoteEntity,
    onNoteClick: () -> Unit,
    onPinClick: () -> Unit,
    onArchiveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { onNoteClick() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                maxLines = 2,
                modifier = Modifier.clickable { onNoteClick() }
            )

            Spacer(modifier = Modifier.height(8.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (note.is_pinned) {
                    Text("📌")
                }

                TextButton(onClick = onPinClick) {
                    Text(if (note.is_pinned) "Unpin" else "Pin")
                }

                TextButton(onClick = onArchiveClick) {
                    Text(if (note.is_archived) "Unarchive" else "Archive")
                }
            }

        }
    }
}
