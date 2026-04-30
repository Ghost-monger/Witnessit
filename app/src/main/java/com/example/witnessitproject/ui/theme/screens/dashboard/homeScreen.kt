package com.example.witnessitproject.ui.theme.screens.dashboard

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.util.*

// ── WitnessIt Tech Palette ───────────────────────────
private val DarkBg      = Color(0xFF05070A)
private val Surface     = Color(0xFF0D1321)
private val CardBg      = Color(0xFF0D1321)
private val Border      = Color(0xFF1E2D5A)
private val Accent      = Color(0xFFFF3D00) // Safety Orange
private val NeonCyan    = Color(0xFF00E5FF) // Tech Blue
private val TextPrimary = Color(0xFFF8FAFC)
private val TextMuted   = Color(0xFF94A3B8)

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
    }

    val filteredReports = if (selectedFilter == "All") reports else reports.filter { it.scamType == selectedFilter }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // --- VISUAL LAYER: Background Glow ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(NeonCyan.copy(0.06f), Color.Transparent)),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = 1000f
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(ROUTE_NEW_REPORT) },
                    containerColor = Accent,
                    shape = RoundedCornerShape(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.Default.Add, "New", tint = Color.White, modifier = Modifier.size(28.dp))
                }
            },
            bottomBar = { BottomNavBar(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                // ── HEADER ──
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            "WITNESS IT",
                            style = TextStyle(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 2.sp,
                                shadow = Shadow(color = NeonCyan.copy(0.4f), blurRadius = 15f)
                            )
                        )
                        Text("LIVE THREAT FEED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan, letterSpacing = 1.sp)
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.AccountCircle, null, tint = TextMuted, modifier = Modifier.size(30.dp))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(Surface)) {
                            DropdownMenuItem(
                                text = { Text("LOGOUT SESSION", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(ROUTE_LOGIN) { popUpTo(0) { inclusive = true } }
                                },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = Accent) }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── SEARCH INTAKE ──
                Surface(
                    modifier = Modifier.fillMaxWidth().height(52.dp).clickable { navController.navigate(ROUTE_SEARCH) },
                    shape = RoundedCornerShape(14.dp),
                    color = Surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("SCAN FOR THREATS...", color = TextMuted, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── FILTERS ──
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            enabled = true,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CardBg,
                                labelColor = TextMuted,
                                selectedContainerColor = Accent,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Border,
                                selectedBorderColor = Accent,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── FEED ──
                if (filteredReports.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛡️", fontSize = 50.sp)
                            Text("DATABASE CLEAR", color = TextPrimary, fontWeight = FontWeight.Black)
                            Text("No threats logged in this category.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
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
fun ReportCard(report: ReportModel, context: Context, viewModel: ReportViewModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                ScamTypeBadge(report.scamType)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (report.verified) Color(0xFF22C55E).copy(0.1f) else Color.White.copy(0.05f)
                ) {
                    Text(
                        if (report.verified) "VERIFIED THREAT" else "UNVERIFIED",
                        color = if (report.verified) Color(0xFF22C55E) else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(report.target, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(report.description, color = TextMuted, fontSize = 13.sp, maxLines = 2, lineHeight = 18.sp)

            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Accent.copy(0.1f))
                        .clickable { viewModel.upvoteReport(report.reportId, context) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = Accent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("${report.upvotes} FLAGS", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(formatTimestamp(report.timestamp), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ScamTypeBadge(type: String) {
    val color = when (type) {
        "M-Pesa" -> Color(0xFFF59E0B)
        "Phone" -> Accent
        "Website" -> NeonCyan
        "Email" -> Color(0xFF22C55E)
        else -> TextMuted
    }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.15f), border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.3f))) {
        Text(type.uppercase(), color = color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, "dashboard"),
            Triple("Scan", Icons.Default.Search, ROUTE_SEARCH),
            Triple("Logs", Icons.Default.List, ROUTE_MY_REPORTS),
            Triple("Profile", Icons.Default.Person, "profile")
        )
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = false, // Add logic to track current route if needed
                onClick = { navController.navigate(route) },
                icon = { Icon(icon, null) },
                label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    unselectedIconColor = TextMuted,
                    indicatorColor = NeonCyan.copy(0.1f)
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