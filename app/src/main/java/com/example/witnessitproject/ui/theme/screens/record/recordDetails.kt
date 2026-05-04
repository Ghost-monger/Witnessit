package com.example.witnessitproject.ui.theme.screens.record

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import com.example.witnessitproject.ui.theme.navigation.ROUTE_EDIT_REPORT
import com.google.firebase.auth.FirebaseAuth

// ── Enhanced WitnessIt Tech Theme ───────────────────────────
private val DarkBg      = Color(0xFF05070A)
private val CardBg      = Color(0xFF0D1321)
private val Border      = Color(0xFF1E2D5A)
private val Accent      = Color(0xFFFF3D00) // Safety Orange
private val NeonCyan    = Color(0xFF00E5FF) // Tech Blue
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@Composable
fun RecordDetailScreen(
    navController: NavController,
    reportId: String
) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    val report = viewModel.reports.find { it.reportId == reportId }

    if (report == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBg), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonCyan)
        }
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("WIPE DATA?", color = Color.White, fontWeight = FontWeight.Black) },
            text = { Text("This record will be permanently deleted from the secure database.", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReport(report.reportId, context)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) { Text("PURGE", color = Accent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("CANCEL", color = TextMuted) }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp).statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NeonCyan)
                }

                Text(
                    text = "INCIDENT DOSSIER",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                )

                Row {
                    IconButton(onClick = {
                        val shareText = "⚠️ SCAM ALERT: ${report.target}\nType: ${report.scamType}\nFlags: ${report.upvotes}\nStay safe with FakeAlert KE!"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via"))
                    }) {
                        Icon(Icons.Default.Share, "Share", tint = Color.White)
                    }

                    if (currentUserId == report.reportedBy) {
                        IconButton(
                            onClick = {
                                navController.navigate("${ROUTE_EDIT_REPORT}/${report.reportId}")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = Accent)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(listOf(NeonCyan.copy(0.05f), Color.Transparent)),
                    center = Offset(size.width * 0.1f, size.height * 0.1f),
                    radius = 800f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Status Badges ────────
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Accent.copy(0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(0.5f))
                    ) {
                        Text(
                            report.scamType.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Accent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    val statusColor = if (report.verified) Color.Green else TextDim
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(0.5f))
                    ) {
                        Text(
                            if (report.verified) "VERIFIED THREAT" else "PENDING ANALYSIS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // ── Target Card ────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("IDENTIFIED TARGET", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = report.target,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                shadow = Shadow(color = NeonCyan.copy(0.3f), blurRadius = 10f)
                            )
                        )
                    }
                }

                // ── Description Card ────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg.copy(0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("INTEL SUMMARY", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(report.description, fontSize = 15.sp, color = Color.White, lineHeight = 24.sp)
                    }
                }

                // ── Evidence Section ────────
                if (report.evidenceUrls.isNotEmpty()) {
                    Column {
                        Text("ATTACHED EVIDENCE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(report.evidenceUrls) { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Evidence",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(220.dp).clip(RoundedCornerShape(20.dp)).border(1.dp, Border, RoundedCornerShape(20.dp))
                                )
                            }
                        }
                    }
                }

                // ── Flag / Upvote Action ────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${report.upvotes}",
                                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Black, color = Accent, shadow = Shadow(Accent.copy(0.4f), blurRadius = 10f))
                            )
                            Text("COMMUNITY FLAGS", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.upvoteReport(report.reportId, context) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Accent)
                        ) {
                            Text("⚠️ FLAG TARGET", fontWeight = FontWeight.Black)
                        }
                    }
                }

                // ── Metadata Card ────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg.copy(0.4f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("SYSTEM LOGS", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Black)
                        HorizontalDivider(color = Border)
                        MetadataRow("LOGGED ON", report.timestamp?.toDate()?.let {
                            java.text.SimpleDateFormat("dd MMM yyyy | HH:mm", java.util.Locale.getDefault()).format(it)
                        } ?: "N/A")
                        MetadataRow("THREAT LEVEL", if (report.verified) "CRITICAL" else "ELEVATED")
                        MetadataRow("DATA SOURCE", "COMMUNITY FEED")
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 11.sp, color = TextDim, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}