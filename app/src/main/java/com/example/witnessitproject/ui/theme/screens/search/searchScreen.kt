package com.example.witnessitproject.ui.theme.screens.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.witnessitproject.data.ReportViewModel
import com.example.witnessitproject.ui.theme.screens.dashboard.ReportCard

// ── Enhanced WitnessIt Tech Theme ───────────────────────────
private val DarkBg      = Color(0xFF05070A)
private val CardBg      = Color(0xFF0D1321)
private val Border      = Color(0xFF1E2D5A)
private val Accent      = Color(0xFFFF3D00) // Safety Orange
private val NeonCyan    = Color(0xFF00E5FF) // Tech Blue
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current

    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "M-Pesa", "Phone", "Website", "Email", "Other")

    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    val searchResults = viewModel.reports.filter { report ->
        val matchesQuery = query.isBlank() ||
                report.target.contains(query, ignoreCase = true) ||
                report.description.contains(query, ignoreCase = true) ||
                report.scamType.contains(query, ignoreCase = true)

        val matchesFilter = selectedFilter == "All" || report.scamType == selectedFilter
        matchesQuery && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- Background Tech Glow ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(NeonCyan.copy(0.05f), Color.Transparent)),
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = 1000f
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar / Search Input ──────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NeonCyan)
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text("SCAN DATABASE...", color = TextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = Accent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Border,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg,
                        cursorColor = Accent
                    )
                )
            }

            // ── Intelligence Filters ────────────────────────
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        enabled = true,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
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

            // ── Results Counter ─────────────────────────────
            if (query.isNotBlank() || selectedFilter != "All") {
                Text(
                    text = "${searchResults.size} MATCHES FOUND IN CLOUD DATABASE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // ── Search Logic States ────────────────────
            when {
                query.isBlank() && selectedFilter == "All" -> {
                    SearchPrompt(text = "SEARCH BEFORE YOU TRUST", subtext = "Initialize scanner by typing a number or URL.")
                }

                searchResults.isEmpty() -> {
                    EmptyState(query, navController)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(searchResults) { report ->
                            ReportCard(
                                report = report,
                                context = context,
                                viewModel = viewModel,
                                onClick = { navController.navigate("report_detail/${report.reportId}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchPrompt(text: String, subtext: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🛡️", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 2.sp)
            )
            Text(
                text = subtext,
                fontSize = 12.sp, color = TextMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState(query: String, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("✅", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NO THREATS DETECTED",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Green, letterSpacing = 1.sp)
            )
            Text(
                text = "\"$query\" is not currently flagged. Proceed with caution and report if suspicious activity occurs.",
                fontSize = 12.sp, color = TextMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("new_report") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("REPORT AS NEW THREAT", fontWeight = FontWeight.Bold)
            }
        }
    }
}