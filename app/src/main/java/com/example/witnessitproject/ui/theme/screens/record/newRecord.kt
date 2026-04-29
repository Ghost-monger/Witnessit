package com.example.witnessitproject.ui.theme.screens.record

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.witnessitproject.data.ReportViewModel

@Composable
fun NewRecordScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // ViewModel — handles all uploads and Firestore saving
    val viewModel: ReportViewModel = viewModel()

    // Form fields
    var scamType by remember { mutableStateOf("M-Pesa") }
    var target by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Image state
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val scamTypes = listOf("M-Pesa", "Phone", "Website", "Email", "Other")

    // Gallery picker — multiple images
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val combined = (selectedImages + uris).distinct().take(5)
        selectedImages = combined
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Report a scam",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scam type chips
        Text("Scam type", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scamTypes) { type ->
                FilterChip(
                    selected = scamType == type,
                    onClick = { scamType = type },
                    label = { Text(type, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFAECE7),
                        selectedLabelColor = Color(0xFF712B13)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target input
        Text("Phone number / website / account", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = target,
            onValueChange = { target = it },
            placeholder = { Text("e.g. 0712345678 or scamsite.co.ke") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description input
        Text("What happened?", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Describe the scam in detail...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image picker section
        Text("Screenshots / evidence (max 5)", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Add photo button — always first
            item {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (selectedImages.size < 5) {
                                galleryLauncher.launch("image/*")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add image",
                            tint = Color.Gray
                        )
                        Text("Add photo", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            // Preview selected images
            items(selectedImages) { uri ->
                Box(modifier = Modifier.size(90.dp)) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    // Remove button
                    IconButton(
                        onClick = {
                            selectedImages = selectedImages.filter { it != uri }
                        },
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${selectedImages.size}/5 photos added",
            fontSize = 11.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button — calls ViewModel directly, no await, no suspend function
        Button(
            onClick = {
                if (target.isBlank() || description.isBlank()) return@Button
                viewModel.submitReport(
                    imageUris = selectedImages,
                    scamType = scamType,
                    target = target,
                    description = description,
                    context = context,
                    navController = navController
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = target.isNotBlank() && description.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF993C1D)
            )
        ) {
            Text("Submit report", color = Color.White)
        }
    }
}