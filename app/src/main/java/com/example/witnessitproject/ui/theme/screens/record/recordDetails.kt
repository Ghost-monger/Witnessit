package com.example.witnessitproject.ui.theme.screens.record

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
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
import com.google.firebase.auth.FirebaseAuth

// ── FakeAlert KE Theme ───────────────────────────
private val DarkBg  = Color(0xFF0A0F1E)
private val CardBg  = Color(0xFF131D3B)
private val Border  = Color(0xFF1E2D5A)
private val Accent  = Color(0xFF993C1D)
private val TextMuted = Color(0xFF7A8AB5)

@Composable
fun RecordDetailScreen(
    navController: NavController,
    reportId: String
) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch reports if list is empty
    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    // Find the specific report from the ViewModel list
    val report = viewModel.reports.find { it.reportId == reportId }

    // Show loading state while report is being fetched
    if (report == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Accent)
        }
        return
    }

    // Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete report?", color = Color.White) },
            text = {
                Text(
                    "This report will be permanently removed. This cannot be undone.",
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReport(report.reportId, context)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Delete", color = Accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            // Top bar with back button, share, and delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Report Detail",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Row {
                    // Share button — shares report as text via Android share sheet
                    IconButton(
                        onClick = {
                            val shareText = buildString {
                                append("⚠️ SCAM ALERT via FakeAlert KE\n\n")
                                append("Type: ${report.scamType}\n")
                                append("Target: ${report.target}\n")
                                append("Details: ${report.description}\n")
                                append("Flagged by: ${report.upvotes} people\n\n")
                                append("Stay safe! Download FakeAlert KE to report scams.")
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(
                                Intent.createChooser(intent, "Share scam alert")
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }

                    // Delete button — only visible to the report owner
                    if (currentUserId == report.reportedBy) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Accent
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Scam type badge + verified status ────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Scam type badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (report.scamType) {
                        "M-Pesa"  -> Color(0xFFFAEEDA)
                        "Phone"   -> Color(0xFFFAECE7)
                        "Website" -> Color(0xFFEEEDFE)
                        "Email"   -> Color(0xFFE1F5EE)
                        else      -> Color(0xFFF1EFE8)
                    }
                ) {
                    Text(
                        text = report.scamType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (report.scamType) {
                            "M-Pesa"  -> Color(0xFF854F0B)
                            "Phone"   -> Color(0xFF712B13)
                            "Website" -> Color(0xFF3C3489)
                            "Email"   -> Color(0xFF0F6E56)
                            else      -> Color(0xFF5F5E5A)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                // Verified badge
                if (report.verified) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0F6E56).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F6E56))
                            )
                            Text(
                                text = "Confirmed scam",
                                fontSize = 11.sp,
                                color = Color(0xFF0F6E56),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Gray.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Unverified",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── Target (phone/website) ───────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "REPORTED TARGET",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = report.target,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // ── Description ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WHAT HAPPENED",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.description,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 22.sp
                    )
                }
            }

            // ── Evidence screenshots ─────────────────────
            if (report.evidenceUrls.isNotEmpty()) {
                Column {
                    Text(
                        text = "EVIDENCE (${report.evidenceUrls.size} screenshot(s))",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(report.evidenceUrls) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Evidence screenshot",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            // ── Upvote section ───────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${report.upvotes}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Accent
                        )
                        Text(
                            text = "people flagged this as a scam",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = { viewModel.upvoteReport(report.reportId, context) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent
                        )
                    ) {
                        Text(
                            text = "⚠️ Flag this",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── Report metadata ──────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "REPORT INFO",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    HorizontalDivider(color = Border)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date reported", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = report.timestamp?.toDate()?.let {
                                java.text.SimpleDateFormat(
                                    "dd MMM yyyy, HH:mm",
                                    java.util.Locale.getDefault()
                                ).format(it)
                            } ?: "Unknown",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Scam type", fontSize = 13.sp, color = TextMuted)
                        Text(report.scamType, fontSize = 13.sp, color = Color.White)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = if (report.verified) "Confirmed" else "Unverified",
                            fontSize = 13.sp,
                            color = if (report.verified) Color(0xFF0F6E56) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}