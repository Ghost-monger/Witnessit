package com.example.witnessitproject.ui.theme.screens.admin


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.witnessitproject.ui.theme.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth

private val DarkBg    = Color(0xFF0A0F1E)
private val CardBg    = Color(0xFF131D3B)
private val Border    = Color(0xFF1E2D5A)
private val Accent    = Color(0xFF993C1D)
private val TextMuted = Color(0xFF7A8AB5)
private val Green     = Color(0xFF1D9E75)

@Composable
fun AdminScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val pendingReports = viewModel.pendingReports

    LaunchedEffect(Unit) {
        viewModel.fetchPendingReports(context)
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Admin Dashboard",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${pendingReports.size} reports awaiting review",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Logout button
            TextButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                Text("Logout", color = Accent)
            }
        }

        HorizontalDivider(color = Border)

        // ── Pending reports list ──────────────────────
        when {
            pendingReports.isEmpty() -> {
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
                            text = "All clear!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "No reports pending review",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = pendingReports,
                        key = { it.reportId }
                    ) { report ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp, Border
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                // Scam type + target
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Accent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = report.scamType,
                                            fontSize = 11.sp,
                                            color = Accent,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Pending review",
                                        fontSize = 11.sp,
                                        color = Color(0xFFF5C842)
                                    )
                                }

                                // Target
                                Text(
                                    text = report.target,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                // Description
                                Text(
                                    text = report.description,
                                    fontSize = 13.sp,
                                    color = TextMuted,
                                    lineHeight = 20.sp,
                                    maxLines = 3,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )

                                // Evidence count
                                if (report.evidenceUrls.isNotEmpty()) {
                                    Text(
                                        text = "${report.evidenceUrls.size} screenshot(s) attached",
                                        fontSize = 11.sp,
                                        color = Color(0xFF7B9FFF)
                                    )
                                }

                                HorizontalDivider(color = Border)

                                // Approve + Reject buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Reject button
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.rejectReport(report.reportId, context)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp, Accent
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Reject",
                                            tint = Accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reject", color = Accent)
                                    }

                                    // Approve button
                                    Button(
                                        onClick = {
                                            viewModel.approveReport(report.reportId, context)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Green
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Approve",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}