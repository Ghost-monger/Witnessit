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

// ── Unified WitnessIt Vibrant Theme ───────────────────────────
private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1) // Primary Action
private val AlertCoral   = Color(0xFFFB7185) // Update Action
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReportScreen(
    navController: NavController,
    reportId: String
) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    val report = viewModel.reports.find { it.reportId == reportId }

    if (report == null) {
        Box(modifier = Modifier.fillMaxSize().background(DeepSpace), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
        return
    }

    var scamType by remember { mutableStateOf(report.scamType) }
    var target by remember { mutableStateOf(report.target) }
    var description by remember { mutableStateOf(report.description) }
    var existingUrls by remember { mutableStateOf(report.evidenceUrls) }
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

    Column(modifier = Modifier.fillMaxSize().background(DeepSpace)) {

        // ── Top Bar ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(CardGlass, CircleShape).border(1.dp, BorderGlass, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ElectricBlue
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "REVISE LOG",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricBlue,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Edit Report",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Category ───────────────────────
            Column {
                SectionLabel("INCIDENT CATEGORY")
                LazyRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(scamTypes) { type ->
                        val isSelected = scamType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { scamType = type },
                            label = { Text(type.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Black) },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardGlass,
                                labelColor = TextMuted,
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BorderGlass,
                                selectedBorderColor = ElectricBlue,
                                borderWidth = 1.dp
                            )
                        )
                    }
                }
            }

            // ── Target ──────────────────────────
            Column {
                SectionLabel("TARGET IDENTIFIER")
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = BorderGlass,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CardGlass,
                        unfocusedContainerColor = CardGlass
                    )
                )
            }

            // ── Description ─────────────────────
            Column {
                SectionLabel("INTEL DETAILS")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = BorderGlass,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CardGlass,
                        unfocusedContainerColor = CardGlass
                    )
                )
            }

            // ── Evidence ──────────────────────
            val totalImages = existingUrls.size + newImages.size
            Column {
                SectionLabel("EVIDENCE ATTACHMENTS ($totalImages/5)")
                LazyRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(CardGlass)
                                .border(BorderStroke(1.dp, if (totalImages < 5) ElectricBlue.copy(0.4f) else BorderGlass), RoundedCornerShape(20.dp))
                                .clickable { if (totalImages < 5) galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, "Add", tint = if (totalImages < 5) ElectricBlue else TextDim)
                        }
                    }

                    // Existing
                    items(existingUrls) { url ->
                        EvidenceBox(model = url, isNew = false) {
                            existingUrls = existingUrls.filter { it != url }
                        }
                    }

                    // New
                    items(newImages) { uri ->
                        EvidenceBox(model = uri, isNew = true) {
                            newImages = newImages.filter { it != uri }
                        }
                    }
                }
            }

            // ── Save button ───────────────────────────
            Button(
                onClick = {
                    if (target.isNotBlank() && description.isNotBlank()) {
                        viewModel.updateReport(reportId, scamType, target, description, newImages, existingUrls, context, navController)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                enabled = target.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AlertCoral,
                    disabledContainerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                Text("SAVE LOG CHANGES", fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun EvidenceBox(model: Any, isNew: Boolean, onRemove: () -> Unit) {
    Box(modifier = Modifier.size(100.dp)) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, if (isNew) ElectricBlue else BorderGlass, RoundedCornerShape(20.dp))
        )
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(24.dp).clickable { onRemove() },
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp))
        }
    }
}