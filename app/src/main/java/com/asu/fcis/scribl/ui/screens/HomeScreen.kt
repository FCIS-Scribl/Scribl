
package com.asu.fcis.scribl.ui.screens

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import com.asu.fcis.scribl.data.ScriblDatabase
import com.asu.fcis.scribl.ui.theme.*

@Composable
fun HomeScreen(
    onAddNoteClick: () -> Unit = {},
    onCategoryClick: (Int) -> Unit = {},
    onAllNotesClick: () -> Unit = {},
    onAddCategoryClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember {
        ScriblDatabase.getDatabase(context)
    }

    val categories by database.categoryDao()
        .getAllCategories()
        .collectAsState(initial = emptyList())

    val notes by database.noteDao()
        .getAllNotes()
        .collectAsState(initial = emptyList())

    val activeNotes = notes.filter { !it.is_archived }

    val categoryColors = listOf(
        DarkBlue, Purple, Coral, Teal
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 45.dp, bottom = 35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SCRIBL",
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText
            )

            Text(
                text = "Welcome back",
                fontSize = 21.sp,
                color = Color.Gray
            )
        }

        // All Notes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable { onAllNotesClick() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Teal
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "All notes",
                    fontSize = 22.sp,
                    color = Color.White
                )

                Text(
                    text = "Notes: ${activeNotes.size}",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        // Categories
        Text(
            text = "Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = DarkText,
            modifier = Modifier.padding(
                top = 32.dp,
                bottom = 10.dp
            )
        )

        categories.chunked(2).forEachIndexed { rowIndex, rowCategories ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowCategories.forEachIndexed { index, category ->

                    val color = categoryColors[
                        (rowIndex * 2 + index) % categoryColors.size
                    ]

                    CategoryCard(
                        name = category.name,
                        count = activeNotes.count {
                            it.category_id == category.id
                        },
                        color = color,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onCategoryClick(category.id)
                        }
                    )
                }

                if (rowCategories.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Add Category
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .clickable { onAddCategoryClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightGray
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 35.sp, color = DarkText)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Note
        Button(
            onClick = onAddNoteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Teal
            )
        ) {
            Text("ADD NOTE", color = Color.White)
        }

        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun CategoryCard(
    name: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(name, fontSize = 22.sp, color = Color.White)
            Text(
                "Notes: $count",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}


