package com.example.witnessitproject.ui.theme.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Shield
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
import com.example.witnessitproject.ui.theme.navigation.ROUTE_AUTHORITY
import com.example.witnessitproject.ui.theme.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth


private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1)
private val NeonEmerald  = Color(0xFF10B981)
private val AlertCoral   = Color(0xFFFB7185)

private val TextMuted    = Color(0xFF94A3B8)

@Composable
fun AdminScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val pendingReports = viewModel.pendingReports
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchPendingReports(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COMMAND CENTER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricBlue,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Admin Review",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            IconButton(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.background(AlertCoral.copy(0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Logout, "Logout", tint = AlertCoral)
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate(ROUTE_AUTHORITY) },
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardGlass),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlass)
            ) {
                Icon(Icons.Default.Shield, null, modifier = Modifier.size(16.dp), tint = ElectricBlue)
                Spacer(Modifier.width(8.dp))
                Text("AUTHORITY VIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Surface(
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(12.dp),
                color = ElectricBlue.copy(0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "${pendingReports.size} PENDING LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))


        if (pendingReports.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(20.dp)
            ) {
                items(items = pendingReports, key = { it.reportId }) { report ->
                    AdminReportCard(
                        scamType = report.scamType,
                        target = report.target,
                        description = report.description,
                        evidenceCount = report.evidenceUrls.size,
                        onApprove = { viewModel.approveReport(report.reportId, context) },
                        onReject = { viewModel.rejectReport(report.reportId, context) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminReportCard(
    scamType: String,
    target: String,
    description: String,
    evidenceCount: Int,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlass),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlass)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricBlue.copy(0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(0.4f))
                ) {
                    Text(
                        scamType.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                if (evidenceCount > 0) {
                    Text("$evidenceCount ATTACHMENTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
            }

            Text(target, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)

            Text(
                description,
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 20.sp,
                maxLines = 3
            )

            HorizontalDivider(color = BorderGlass, thickness = 0.5.dp)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertCoral.copy(0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertCoral.copy(0.5f))
                ) {
                    Icon(Icons.Default.Close, null, tint = AlertCoral, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("REJECT", color = AlertCoral, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("APPROVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = NeonEmerald.copy(0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(0.3f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(40.dp), tint = NeonEmerald)
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("SECURE & CLEAR", fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp)
            Text("No reports are pending review", color = TextMuted, fontSize = 14.sp)
        }
    }
}