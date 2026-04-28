package com.example.witnessitproject.ui.theme.screens.dashboard

import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import com.example.witnessitproject.ui.theme.data.ReportViewModel
import com.example.witnessitproject.ui.theme.models.ReportModel
import com.example.witnessitproject.ui.theme.navigation.ROUTE_NEW_REPORT
import com.example.witnessitproject.ui.theme.navigation.ROUTE_SEARCH
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val reports = viewModel.reports

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "M-Pesa", "Phone", "Website", "Email", "Other")

    // Fetch reports when screen first loads
    LaunchedEffect(Unit) {
        viewModel.fetchReports(context)
    }

    // Filter reports based on selected chip
    val filteredReports = if (selectedFilter == "All") {
        reports
    } else {
        reports.filter { it.scamType == selectedFilter }
    }

    Scaffold(
        // Floating action button — Report a scam
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUTE_NEW_REPORT) },
                shape = CircleShape,
                containerColor = Color(0xFF993C1D)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Report a scam",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,

        // Bottom navigation bar
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FakeAlert KE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { navController.navigate(ROUTE_SEARCH) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search bar — tapping navigates to search screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { navController.navigate(ROUTE_SEARCH) }
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search a number or website...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false, // tapping navigates, not types
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFAECE7),
                            selectedLabelColor = Color(0xFF712B13)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reports feed
            if (filteredReports.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No reports yet",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Be the first to report a scam",
                            fontSize = 13.sp,
                            color = Color.LightGray
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredReports) { report ->
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

// Individual report card
@Composable
fun ReportCard(
    report: ReportModel,
    context: Context,
    viewModel: ReportViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Top row — badge + verified status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScamTypeBadge(scamType = report.scamType)
                if (report.verified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .then(
                                    Modifier.padding(0.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFF0F6E56),
                                shape = CircleShape
                            ) {}
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Confirmed",
                            fontSize = 11.sp,
                            color = Color(0xFF0F6E56),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Unverified",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Target — phone number or website
            Text(
                text = report.target,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Description — capped at 2 lines
            Text(
                text = report.description,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            // Evidence image count indicator
            if (report.evidenceUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${report.evidenceUrls.size} screenshot(s) attached",
                    fontSize = 11.sp,
                    color = Color(0xFF534AB7)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row — upvote + timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upvote button
                OutlinedButton(
                    onClick = { viewModel.upvoteReport(report.reportId, context) },
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp, Color(0xFFE0E0E0)
                    )
                ) {
                    Text(
                        text = "${report.upvotes} flagged this",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Timestamp
                Text(
                    text = formatTimestamp(report.timestamp),
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

// Coloured badge based on scam type
@Composable
fun ScamTypeBadge(scamType: String) {
    val (bgColor, textColor) = when (scamType) {
        "M-Pesa" -> Color(0xFFFAEEDA) to Color(0xFF854F0B)
        "Phone"  -> Color(0xFFFAECE7) to Color(0xFF712B13)
        "Website" -> Color(0xFFEEEDFE) to Color(0xFF3C3489)
        "Email"  -> Color(0xFFE1F5EE) to Color(0xFF0F6E56)
        else     -> Color(0xFFF1EFE8) to Color(0xFF5F5E5A)
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            text = scamType,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

// Format Firebase Timestamp to readable string
fun formatTimestamp(timestamp: Timestamp?): String {
    timestamp ?: return "Unknown"
    val now = Date()
    val date = timestamp.toDate()
    val diffMs = now.time - date.time
    val diffHrs = diffMs / (1000 * 60 * 60)
    val diffDays = diffMs / (1000 * 60 * 60 * 24)

    return when {
        diffHrs < 1 -> "Just now"
        diffHrs < 24 -> "${diffHrs}hrs ago"
        diffDays == 1L -> "Yesterday"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
    }
}

// Bottom navigation bar
@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = {
                Icon(
                    androidx.compose.material.icons.Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "search",
            onClick = { navController.navigate(ROUTE_SEARCH) },
            icon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text("Search", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "my_reports",
            onClick = { navController.navigate("my_reports") },
            icon = {
                Icon(
                    androidx.compose.material.icons.Icons.Default.List,
                    contentDescription = "My Reports"
                )
            },
            label = { Text("My reports", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") },
            icon = {
                Icon(
                    androidx.compose.material.icons.Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 10.sp) }
        )
    }
}