package com.example.witnessitproject.ui.theme.screens.admin

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
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
import com.example.witnessitproject.ui.theme.models.ReportModel
import java.text.SimpleDateFormat
import java.util.*

// ── Unified WitnessIt Vibrant Theme ───────────────────────────
private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1) // Primary
private val NeonEmerald  = Color(0xFF10B981) // Success/Forward
private val AlertCoral   = Color(0xFFFB7185) // Critical Priority

private val TextMuted    = Color(0xFF94A3B8)
private val TextDim      = Color(0xFF475569)

@Composable
fun AuthorityScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val highPriorityReports = viewModel.highPriorityReports

    LaunchedEffect(Unit) {
        viewModel.fetchHighPriorityReports(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ElectricBlue)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "CRITICAL UPLINK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = AlertCoral,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Authority Portal",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        // ── Critical Info Banner ───────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = AlertCoral.copy(0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AlertCoral.copy(0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, null, tint = AlertCoral, modifier = Modifier.size(24.dp))
                Text(
                    text = "High-priority logs (50+ community flags) requiring immediate intervention.",
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Reports List ──────────────────────────────
        if (highPriorityReports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Gavel, null, modifier = Modifier.size(60.dp), tint = TextDim)
                    Spacer(Modifier.height(16.dp))
                    Text("NO CRITICAL THREATS", fontWeight = FontWeight.Black, color = Color.White)
                    Text("System is currently stable", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(20.dp)
            ) {
                items(items = highPriorityReports, key = { it.reportId }) { report ->
                    AuthorityReportCard(
                        report = report,
                        onShare = {
                            val formalReport = buildFormalReport(report)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "WITNESS IT — URGENT CRIME DOSSIER")
                                putExtra(Intent.EXTRA_TEXT, formalReport)
                            }
                            context.startActivity(Intent.createChooser(intent, "Transmit to Authorities..."))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AuthorityReportCard(report: ReportModel, onShare: () -> Unit) {
    val isExtreme = report.upvotes >= 100

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlass),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExtreme) AlertCoral.copy(0.6f) else BorderGlass
        )
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricBlue.copy(0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(0.4f))
                ) {
                    Text(
                        report.scamType.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isExtreme) AlertCoral.copy(0.15f) else Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isExtreme) AlertCoral else TextDim)
                ) {
                    Text(
                        "⚠️ ${report.upvotes} FLAGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isExtreme) AlertCoral else TextMuted,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Text(
                text = report.target,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = report.description,
                fontSize = 14.sp,
                color = TextMuted,
                lineHeight = 22.sp
            )

            HorizontalDivider(color = BorderGlass, thickness = 0.5.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "LOGGED: ${report.timestamp?.toDate()?.let {
                        SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(it)
                    } ?: "N/A"}",
                    fontSize = 11.sp,
                    color = TextDim,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onShare,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("TRANSMIT", fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

fun buildFormalReport(report: ReportModel): String {
    val date = report.timestamp?.toDate()?.let {
        SimpleDateFormat("dd MMMM yyyy 'at' HH:mm", Locale.getDefault()).format(it)
    } ?: "Unknown date"

    return """
WITNESS IT KE — OFFICIAL CRIME DOSSIER
=====================================
Transmission Date: ${SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())}

INCIDENT SUMMARY
-----------------
Threat Type    : ${report.scamType}
Target/ID      : ${report.target}
Reported On    : $date
Community Flags: ${report.upvotes} verified alerts

DETAILED INTEL
---------------
${report.description}

EVIDENCE REGISTRY
------------------
${if (report.evidenceUrls.isNotEmpty())
        "${report.evidenceUrls.size} screenshot(s) securely logged.\nAccess URLs:\n${report.evidenceUrls.joinToString("\n")}"
    else
        "No digital evidence attached."
    }

RECOMMENDED ACTION
-------------------
This entity has exceeded the critical community flag threshold (${report.upvotes} flags).
Immediate investigation is requested.

- M-Pesa Fraud: Safaricom Fraud Unit (0722 000 000)
- Digital Crime: DCI Cyber Unit (cybercrime@dci.go.ke)
- Telecom Abuse: Communications Authority (info@ca.go.ke)

=====================================
Generated via WitnessIt Secure Uplink.
Protecting the Digital Frontier.
    """.trimIndent()
}