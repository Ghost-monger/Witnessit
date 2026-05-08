package com.example.witnessitproject.ui.theme.screens.dashboard

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.witnessitproject.data.ReportViewModel
import com.example.witnessitproject.ui.theme.models.ReportModel
import com.example.witnessitproject.ui.theme.navigation.*
import com.google.firebase.auth.FirebaseAuth
import java.util.*

// ── WitnessIt Vibrant Palette ───────────────────────────
private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1) // Primary Action/Trust
private val NeonEmerald  = Color(0xFF10B981) // Safety/Verified
private val AlertCoral   = Color(0xFFFB7185) // Threat/Danger
private val MpesaGold    = Color(0xFFF59E0B)

private val TextPrimary  = Color(0xFFF8FAFC)
private val TextMuted    = Color(0xFF94A3B8)
private val TextDim      = Color(0xFF64748B)

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val reports = viewModel.reports



    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "M-Pesa", "Phone", "Website", "Email", "Other")
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchReports(context)
        viewModel.fetchUserUpvotes(context)
    }

    val filteredReports = if (selectedFilter == "All") reports else reports.filter { it.scamType == selectedFilter }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        // --- VISUAL LAYER: Background "Lurking" Glow ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.07f), Color.Transparent)
                ),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = 1000f
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(ROUTE_NEW_REPORT) },
                    containerColor = AlertCoral,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp)
                ) {
                    Icon(Icons.Default.Add, "New Report", modifier = Modifier.size(30.dp))
                }
            },

        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // ── HEADER ──
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "WITNESS IT",
                            style = TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 1.5.sp,
                                shadow = Shadow(color = ElectricBlue.copy(0.4f), blurRadius = 15f)
                            )
                        )
                        Text(
                            "LIVE THREAT FEED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricBlue,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier
                                .background(CardGlass, CircleShape)
                                .border(1.dp, BorderGlass, CircleShape)
                        ) {
                            Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(CardGlass).border(1.dp, BorderGlass)
                        ) {
                            DropdownMenuItem(
                                text = { Text("LOGOUT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(ROUTE_LOGIN) { popUpTo(0) { inclusive = true } }
                                },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = AlertCoral) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── SEARCH INTAKE ──
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clickable { navController.navigate(ROUTE_SEARCH) },
                    shape = RoundedCornerShape(18.dp),
                    color = CardGlass,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlass)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(14.dp))
                        Text("SCAN DATABASE FOR THREATS...", color = TextDim, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── FILTERS ──
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            enabled = true,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardGlass,
                                labelColor = TextMuted,
                                selectedContainerColor = ElectricBlue,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = BorderGlass,
                                selectedBorderColor = ElectricBlue,
                                borderWidth = 1.dp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── FEED ──
                if (filteredReports.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(bottom = 80.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛡️", fontSize = 60.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("DATABASE CLEAR", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("No threats logged in this category.", color = TextDim, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredReports) { report ->
                            ReportCard(report, context, viewModel) {
                                navController.navigate("report_detail/${report.reportId}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: ReportModel, context: Context, viewModel: ReportViewModel, onClick: () -> Unit)
{val isUpvoted = viewModel.upvotedReportIds.contains(report.reportId)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlass),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlass)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                ScamTypeBadge(report.scamType)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (report.verified) NeonEmerald.copy(0.15f) else Color.White.copy(0.05f),
                    border = if (report.verified) androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(0.3f)) else null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                        if (report.verified) {
                            Icon(Icons.Default.CheckCircle, null, tint = NeonEmerald, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                        }
                        Text(
                            if (report.verified) "VERIFIED THREAT" else "UNVERIFIED",
                            color = if (report.verified) NeonEmerald else TextDim,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(report.target, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                report.description,
                color = TextMuted,
                fontSize = 14.sp,
                maxLines = 2,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isUpvoted) AlertCoral.copy(0.3f)  // ✅ darker when flagged
                            else AlertCoral.copy(0.12f)
                        )
                        .clickable { viewModel.upvoteReport(report.reportId, context) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        null,
                        tint = AlertCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isUpvoted) "✓ ${report.upvotes} FLAGGED"
                        else "${report.upvotes} FLAGS",
                        color = AlertCoral,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(formatTimestamp(report.timestamp), color = TextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScamTypeBadge(type: String) {
    val color = when (type) {
        "M-Pesa" -> MpesaGold
        "Phone" -> AlertCoral
        "Website" -> ElectricBlue
        "Email" -> Color(0xFF818CF8)
        else -> TextMuted
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.4f))
    ) {
        Text(
            type.uppercase(),
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = CardGlass,
        tonalElevation = 0.dp,
        modifier = Modifier.border(1.dp, BorderGlass, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, "dashboard"),
            Triple("Search", Icons.Default.Search, ROUTE_SEARCH),
            Triple("My Reports", Icons.Default.List, ROUTE_MY_REPORTS),
            Triple("Profile", Icons.Default.Person, "profile")
        )
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(route) },
                icon = { Icon(icon, null) },
                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricBlue,
                    unselectedIconColor = TextDim,
                    indicatorColor = ElectricBlue.copy(0.1f)
                )
            )
        }
    }
}

@Composable
fun formatTimestamp(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "JUST NOW"

    val now = Date().time
    val reportTime = timestamp.toDate().time
    val diffMillis = now - reportTime

    val seconds = diffMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "SIGNAL RECENT"
        minutes < 60 -> "${minutes}M AGO"
        hours < 24   -> "${hours}H AGO"
        days < 7     -> "${days}D AGO"
        else -> {
            val sdf = java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
            sdf.format(timestamp.toDate())
        }
    }
}