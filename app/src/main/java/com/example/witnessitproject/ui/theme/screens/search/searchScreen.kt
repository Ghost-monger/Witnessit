package com.example.witnessitproject.ui.theme.screens.search

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.witnessitproject.data.ReportViewModel
import com.example.witnessitproject.ui.theme.screens.dashboard.ReportCard
import com.example.witnessitproject.ui.theme.screens.dashboard.ScamTypeBadge

// ── FakeAlert KE Theme ───────────────────────────
private val DarkBg    = Color(0xFF0A0F1E)
private val CardBg    = Color(0xFF131D3B)
private val Border    = Color(0xFF1E2D5A)
private val Accent    = Color(0xFF993C1D)
private val TextMuted = Color(0xFF7A8AB5)
private val TextDim   = Color(0xFF5A6A90)

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current

    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "M-Pesa", "Phone", "Website", "Email", "Other")

    // Fetch all reports on load
    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    // Filter logic — searches target, description, and scamType
    val searchResults = viewModel.reports.filter { report ->
        val matchesQuery = query.isBlank() ||
                report.target.contains(query, ignoreCase = true) ||
                report.description.contains(query, ignoreCase = true) ||
                report.scamType.contains(query, ignoreCase = true)

        val matchesFilter = selectedFilter == "All" || report.scamType == selectedFilter

        matchesQuery && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {

        // ── Top bar ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Back button
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Search bar — auto focused
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        "Search number, website, keyword...",
                        color = TextDim,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = Border,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Accent
                )
            )
        }

        // ── Filter chips ─────────────────────────────
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFAECE7),
                        selectedLabelColor = Color(0xFF712B13)
                    )
                )
            }
        }

        // ── Results count ─────────────────────────────
        if (query.isNotBlank() || selectedFilter != "All") {
            Text(
                text = "${searchResults.size} result(s) found",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        // ── Results / Empty states ────────────────────
        when {
            // Nothing typed yet — show prompt
            query.isBlank() && selectedFilter == "All" -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Text(
                            text = "Search before you trust",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "Type a phone number, website,\nor keyword to check if it's been flagged",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Search returned no results
            searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✅", fontSize = 48.sp)
                        Text(
                            text = "No reports found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "\"$query\" hasn't been flagged yet.\nStay cautious — if something feels off, report it.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate("new_report") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Accent)
                        ) {
                            Text("Report this number/site", color = Color.White)
                        }
                    }
                }
            }

            // Results found — show report cards
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(searchResults) { report ->
                        ReportCard(
                            report = report,
                            context = context,
                            viewModel = viewModel,
                            onClick = {
                                navController.navigate("report_detail/${report.reportId}")
                            }
                        )
                    }
                }
            }
        }
    }
}