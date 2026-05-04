package com.example.witnessitproject.ui.theme.screens.record

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.witnessitproject.data.ReportViewModel

// ── Enhanced WitnessIt Tech Theme ───────────────────────────
private val DarkBg      = Color(0xFF05070A)
private val CardBg      = Color(0xFF0D1321)
private val Border      = Color(0xFF1E2D5A)
private val Accent      = Color(0xFFFF3D00) // Safety Orange
private val NeonCyan    = Color(0xFF00E5FF) // Tech Blue
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecordScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val viewModel: ReportViewModel = viewModel()

    var scamType by remember { mutableStateOf("M-Pesa") }
    var target by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val scamTypes = listOf("M-Pesa", "Phone", "Website", "Email", "Other")

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = (selectedImages + uris).distinct().take(5)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- Background Tech Glow ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.9f),
                    radius = 900f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Header
            Text(
                text = "FILE INCIDENT REPORT",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    shadow = Shadow(color = Accent.copy(0.4f), blurRadius = 10f)
                )
            )
            Text(
                text = "SYSTEM ENCRYPTION: ACTIVE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Scam type selection
            SectionLabel("SELECT INCIDENT CATEGORY")
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(scamTypes) { type ->
                    val isSelected = scamType == type
                    FilterChip(
                        selected = isSelected,
                        enabled = true,
                        onClick = { scamType = type },
                        label = {
                            Text(
                                text = type,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CardBg,
                            labelColor = TextMuted,
                            selectedContainerColor = Accent,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Border,
                            selectedBorderColor = Accent,
                            borderWidth = 1.dp,
                            selectedBorderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target input
            SectionLabel("IDENTIFIED TARGET (PHONE / URL)")
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                placeholder = { Text("e.g. 0712XXXXXX", color = TextDim) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Border,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description input
            SectionLabel("REPORT LOG DETAILS")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Provide detailed information regarding the suspicious activity...", color = TextDim) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Border,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg
                )

            )

            Spacer(modifier = Modifier.height(20.dp))

            // Image picker section
            SectionLabel("EVIDENCE ATTACHMENTS (${selectedImages.size}/5)")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardBg)
                            .border(BorderStroke(1.dp, if(selectedImages.size < 5) NeonCyan.copy(0.5f) else Border), RoundedCornerShape(16.dp))
                            .clickable { if (selectedImages.size < 5) galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, "Add", tint = if(selectedImages.size < 5) NeonCyan else TextDim)
                            Text("UPLOAD", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if(selectedImages.size < 5) NeonCyan else TextDim)
                        }
                    }
                }

                items(selectedImages) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Evidence",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).border(1.dp, Border, RoundedCornerShape(16.dp))
                        )
                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp).clickable { selectedImages = selectedImages.filter { it != uri } },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = {
                    if (target.isNotBlank() && description.isNotBlank()) {
                        viewModel.submitReport(selectedImages, scamType, target, description, context, navController)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = target.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    disabledContainerColor = CardBg
                )
            ) {
                Text(
                    "TRANSMIT REPORT",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = if (target.isNotBlank() && description.isNotBlank()) Color.White else TextDim
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp
    )
}