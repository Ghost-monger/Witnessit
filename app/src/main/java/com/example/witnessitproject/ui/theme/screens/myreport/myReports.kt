package com.example.witnessitproject.ui.theme.screens.myreport

import androidx.compose.foundation.Canvas
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
private val Accent      = Color(0xFFFF3D00) // Safety Orange/Red
private val NeonCyan    = Color(0xFF00E5FF)
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@Composable
fun MyReportsScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val myReports = viewModel.myReports

    LaunchedEffect(Unit) {
        viewModel.fetchMyReports(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- VISUAL LAYER: Background Tech Glow ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.2f),
                    radius = 800f
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar (Tech Styled) ───────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                    Column {
                        Text(
                            text = "DATA LOGS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "My Reports",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                if (myReports.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Accent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${myReports.size} ENTRIES",
                            fontSize = 10.sp,
                            color = Accent,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── Stats Row (Telemetry Style) ──────────────────────
            if (myReports.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${myReports.size}",
                        label = "SUBMITTED",
                        glowColor = NeonCyan
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${myReports.sumOf { it.upvotes }}",
                        label = "FLAGS",
                        glowColor = Accent
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${myReports.count { it.verified }}",
                        label = "VERIFIED",
                        glowColor = Color.Green
                    )
                }
            }

            // ── Reports List / Empty State ────────────────
            when {
                myReports.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text("📡", fontSize = 56.sp)
                            Text(
                                text = "NO DATA FOUND",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                            )
                            Text(
                                text = "Your intelligence log is empty. Scan and report suspicious activity to secure the network.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate("new_report") },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Accent)
                            ) {
                                Text("INITIALIZE REPORT", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(
                            items = myReports,
                            key = { it.reportId }
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
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    glowColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = glowColor,
                    shadow = Shadow(color = glowColor.copy(alpha = 0.5f), blurRadius = 10f)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDim,
                letterSpacing = 1.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}