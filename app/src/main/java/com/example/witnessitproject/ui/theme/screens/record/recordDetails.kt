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

// ── Unified WitnessIt Vibrant Theme ───────────────────────────
private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1) // Primary Action
private val NeonEmerald  = Color(0xFF10B981) // Verified Status
private val AlertCoral   = Color(0xFFFB7185) // Alert/Danger/Delete

private val TextPrimary  = Color(0xFFF8FAFC)
private val TextMuted    = Color(0xFF94A3B8)
private val TextDim      = Color(0xFF475569)

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
        Box(modifier = Modifier.fillMaxSize().background(DeepSpace), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("WIPE DATA?", color = Color.White, fontWeight = FontWeight.Black) },
            text = { Text("This record will be permanently purged from the secure database.", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReport(report.reportId, context)
                    showDeleteDialog = false
                    navController.popBackStack()
                }) { Text("PURGE", color = AlertCoral, fontWeight = FontWeight.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("CANCEL", color = TextMuted) }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        containerColor = DeepSpace,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(CardGlass, CircleShape).border(1.dp, BorderGlass, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ElectricBlue)
                }

                Text(
                    text = "INCIDENT DOSSIER",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            val shareText = "⚠️ WITNESS IT ALERT: ${report.target}\nType: ${report.scamType}\nVerified: ${report.verified}\nReported via WitnessIt KE"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Dossier"))
                        },
                        modifier = Modifier.background(CardGlass, CircleShape).border(1.dp, BorderGlass, CircleShape)
                    ) {
                        Icon(Icons.Default.Share, "Share", tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    if (currentUserId == report.reportedBy) {
                        IconButton(
                            onClick = { navController.navigate("${ROUTE_EDIT_REPORT}/${report.reportId}") },
                            modifier = Modifier.background(CardGlass, CircleShape).border(1.dp, BorderGlass, CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, "Edit", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.background(AlertCoral.copy(0.1f), CircleShape).border(1.dp, AlertCoral.copy(0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, "Delete", tint = AlertCoral, modifier = Modifier.size(20.dp))
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
                    brush = Brush.radialGradient(listOf(ElectricBlue.copy(0.06f), Color.Transparent)),
                    center = Offset(size.width * 0.1f, size.height * 0.1f),
                    radius = 900f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Status Badges ────────
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusBadge(text = report.scamType.uppercase(), color = ElectricBlue)

                    val statusColor = if (report.verified) NeonEmerald else AlertCoral
                    StatusBadge(
                        text = if (report.verified) "VERIFIED THREAT" else "UNVERIFIED REPORT",
                        color = statusColor
                    )
                }

                // ── Target Card ────────
                DetailCard {
                    Text("IDENTIFIED TARGET", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.target,
                        style = TextStyle(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            shadow = Shadow(color = ElectricBlue.copy(0.4f), blurRadius = 15f)
                        )
                    )
                }

                // ── Description Card ────────
                DetailCard(containerColor = CardGlass.copy(0.5f)) {
                    Text("INTEL SUMMARY", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(report.description, fontSize = 15.sp, color = Color.White, lineHeight = 24.sp)
                }

                // ── Evidence Section ────────
                if (report.evidenceUrls.isNotEmpty()) {
                    Column {
                        Text("ATTACHED EVIDENCE", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            items(report.evidenceUrls) { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Evidence",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(240.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .border(1.dp, BorderGlass, RoundedCornerShape(24.dp))
                                )
                            }
                        }
                    }
                }

                // ── Community Flag Action ────────
                DetailCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${report.upvotes}",
                                style = TextStyle(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AlertCoral,
                                    shadow = Shadow(AlertCoral.copy(0.4f), blurRadius = 15f)
                                )
                            )
                            Text("COMMUNITY FLAGS", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Black)
                        }
                        val isUpvoted = viewModel.upvotedReportIds.contains(report.reportId)

                        Button(
                            onClick = { viewModel.upvoteReport(report.reportId, context) },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isUpvoted) AlertCoral.copy(0.5f) else AlertCoral
                            ),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text(
                                text = if (isUpvoted) "✓ FLAGGED" else "⚠️ FLAG TARGET",
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // ── Metadata Card ────────
                DetailCard(containerColor = Color.Transparent) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("SYSTEM METRICS", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        HorizontalDivider(color = BorderGlass)
                        MetadataRow("LOGGED ON", report.timestamp?.toDate()?.let {
                            java.text.SimpleDateFormat("dd MMM yyyy | HH:mm", java.util.Locale.getDefault()).format(it)
                        } ?: "PENDING...")
                        MetadataRow("THREAT LEVEL", if (report.verified) "CRITICAL" else "ELEVATED")
                        MetadataRow("SOURCE", "COMMUNITY UPLINK")
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.4f))
    ) {
        Text(
            text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = color,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun DetailCard(
    containerColor: Color = CardGlass,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlass)
    ) {
        Column(modifier = Modifier.padding(24.dp), content = content)
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = TextDim, fontWeight = FontWeight.Black)
        Text(value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}