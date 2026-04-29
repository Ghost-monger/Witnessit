package com.example.witnessitproject.ui.theme.screens.myreport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

// ── FakeAlert KE Theme ───────────────────────────
private val DarkBg    = Color(0xFF0A0F1E)
private val CardBg    = Color(0xFF131D3B)
private val Border    = Color(0xFF1E2D5A)
private val Accent    = Color(0xFF993C1D)
private val TextMuted = Color(0xFF7A8AB5)

@Composable
fun MyReportsScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val myReports = viewModel.myReports

    // Fetch user's own reports on load
    LaunchedEffect(Unit) {
        viewModel.fetchMyReports(context)
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
                    text = "My Reports",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // Report count badge
            if (myReports.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Accent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${myReports.size} report(s)",
                        fontSize = 12.sp,
                        color = Accent,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // ── Stats row ────────────────────────────────
        if (myReports.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total reports stat
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${myReports.size}",
                    label = "Reports submitted"
                )

                // Total upvotes across all my reports
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${myReports.sumOf { it.upvotes }}",
                    label = "Total flags received"
                )

                // Verified count
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${myReports.count { it.verified }}",
                    label = "Confirmed scams"
                )
            }
        }

        // ── Reports list / Empty state ────────────────
        when {
            myReports.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text("📋", fontSize = 48.sp)
                        Text(
                            text = "No reports yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "You haven't reported any scams yet.\nHelp protect the community by reporting suspicious numbers or websites.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navController.navigate("new_report") },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Accent
                            )
                        ) {
                            Text("Report a scam", color = Color.White)
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = myReports,
                        key = { it.reportId } // stable keys for smooth animations
                    ) { report ->
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

// Small stat card used in the stats row
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Accent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}