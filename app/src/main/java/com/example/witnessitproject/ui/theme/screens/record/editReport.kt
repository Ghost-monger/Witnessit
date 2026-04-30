package com.example.witnessitproject.ui.theme.screens.record

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

// ── FakeAlert KE Theme ───────────────────────────
private val DarkBg2   = Color(0xFF0A0F1E)
private val CardBg2   = Color(0xFF131D3B)
private val Border2   = Color(0xFF1E2D5A)
private val Accent2   = Color(0xFF993C1D)
private val NeonCyan2 = Color(0xFF00E5FF)
private val TextMuted2 = Color(0xFF7A8AB5)
private val TextDim2  = Color(0xFF5A6A90)

@Composable
fun EditReportScreen(
    navController: NavController,
    reportId: String
) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current

    // Fetch reports if empty
    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    // Find the report to edit
    val report = viewModel.reports.find { it.reportId == reportId }

    // Show loading while fetching
    if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg2),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Accent2)
        }
        return
    }

    // Pre-fill form with existing report data
    var scamType by remember { mutableStateOf(report.scamType) }
    var target by remember { mutableStateOf(report.target) }
    var description by remember { mutableStateOf(report.description) }

    // Existing Cloudinary URLs — kept unless user removes them
    var existingUrls by remember { mutableStateOf(report.evidenceUrls) }

    // New images picked from gallery
    var newImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val scamTypes = listOf("M-Pesa", "Phone", "Website", "Email", "Other")

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        val total = existingUrls.size + newImages.size + uris.size
        if (total <= 5) {
            newImages = (newImages + uris).distinct()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg2)
    ) {

        // ── Top bar ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Edit Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Scam type chips ───────────────────────
            Text(
                "INCIDENT CATEGORY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted2,
                letterSpacing = 1.sp
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(scamTypes) { type ->
                    val isSelected = scamType == type
                    FilterChip(
                        selected = isSelected,
                        enabled = true,
                        onClick = { scamType = type },
                        label = {
                            Text(
                                type,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CardBg2,
                            labelColor = TextMuted2,
                            selectedContainerColor = Accent2,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Border2,
                            selectedBorderColor = Accent2,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }

            // ── Target field ──────────────────────────
            Text(
                "TARGET (PHONE / URL)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted2,
                letterSpacing = 1.sp
            )
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                placeholder = { Text("e.g. 0712XXXXXX", color = TextDim2) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan2,
                    unfocusedBorderColor = Border2,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardBg2,
                    unfocusedContainerColor = CardBg2
                )
            )

            // ── Description field ─────────────────────
            Text(
                "INCIDENT DETAILS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted2,
                letterSpacing = 1.sp
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        "Describe what happened...",
                        color = TextDim2
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan2,
                    unfocusedBorderColor = Border2,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardBg2,
                    unfocusedContainerColor = CardBg2
                )
            )

            // ── Evidence section ──────────────────────
            val totalImages = existingUrls.size + newImages.size
            Text(
                "EVIDENCE ($totalImages/5)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted2,
                letterSpacing = 1.sp
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Add button
                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardBg2)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (totalImages < 5) NeonCyan2.copy(0.5f) else Border2
                                ),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (totalImages < 5) galleryLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = if (totalImages < 5) NeonCyan2 else TextDim2
                            )
                            Text(
                                "ADD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (totalImages < 5) NeonCyan2 else TextDim2
                            )
                        }
                    }
                }

                // Existing Cloudinary images — removable
                items(existingUrls) { url ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Existing evidence",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Border2, RoundedCornerShape(12.dp))
                        )
                        // Remove existing image
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .clickable {
                                    existingUrls = existingUrls.filter { it != url }
                                },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                // New images picked from gallery
                items(newImages) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "New evidence",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, NeonCyan2.copy(0.3f), RoundedCornerShape(12.dp))
                        )
                        // Remove new image
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .clickable {
                                    newImages = newImages.filter { it != uri }
                                },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }

            // Cyan border = new image, dark border = existing image
            Text(
                text = "Cyan border = newly added  |  Dark border = existing",
                fontSize = 10.sp,
                color = TextDim2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Save button ───────────────────────────
            Button(
                onClick = {
                    if (target.isNotBlank() && description.isNotBlank()) {
                        viewModel.updateReport(
                            reportId = reportId,
                            scamType = scamType,
                            target = target,
                            description = description,
                            imageUris = newImages,
                            existingUrls = existingUrls,
                            context = context,
                            navController = navController
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = target.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent2,
                    disabledContainerColor = CardBg2
                )
            ) {
                Text(
                    "SAVE CHANGES",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = if (target.isNotBlank() && description.isNotBlank())
                        Color.White else TextMuted2
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}