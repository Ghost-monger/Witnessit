package com.example.witnessitproject.ui.theme.screens.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.example.witnessitproject.ui.theme.navigation.ROUTE_NEW_REPORT
import com.example.witnessitproject.ui.theme.screens.dashboard.ReportCard


private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1)
private val NeonEmerald  = Color(0xFF10B981)
private val AlertCoral   = Color(0xFFFB7185)

private val TextPrimary  = Color(0xFFF8FAFC)
private val TextMuted    = Color(0xFF94A3B8)
private val TextDim      = Color(0xFF64748B)

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current

    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "M-Pesa", "Phone", "Website", "Email", "Other")

    LaunchedEffect(Unit) {
        if (viewModel.reports.isEmpty()) {
            viewModel.fetchReports(context)
        }
    }

    val searchResults = viewModel.reports.filter { report ->
        val matchesQuery = query.isBlank() ||
                report.target.contains(query, ignoreCase = true) ||
                report.description.contains(query, ignoreCase = true) ||
                report.scamType.contains(query, ignoreCase = true)

        val matchesFilter = selectedFilter == "All" || report.scamType == selectedFilter
        matchesQuery && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.05f), Color.Transparent)
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = 1200f
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(CardGlass, CircleShape).border(1.dp, BorderGlass, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ElectricBlue)
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text("INTEL SCANNER...", color = TextDim, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = ElectricBlue)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = BorderGlass,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CardGlass,
                        unfocusedContainerColor = CardGlass,
                        cursorColor = ElectricBlue
                    )
                )
            }


            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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


            if (query.isNotBlank() || selectedFilter != "All") {
                Text(
                    text = "${searchResults.size} MATCHES IN LOCAL REGISTRY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricBlue,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }


            when {
                query.isBlank() && selectedFilter == "All" -> {
                    SearchPrompt(
                        text = "SEARCH BEFORE YOU TRUST",
                        subtext = "Input a phone number, URL, or name to verify safety."
                    )
                }

                searchResults.isEmpty() -> {
                    EmptyState(query, navController)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
                    ) {
                        items(searchResults) { report ->
                            ReportCard(
                                report = report,
                                context = context,
                                viewModel = viewModel,
                                onClick = { navController.navigate("report_detail/${report.reportId}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchPrompt(text: String, subtext: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("🛡️", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    shadow = Shadow(color = ElectricBlue.copy(0.4f), blurRadius = 15f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtext,
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState(query: String, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Text("✅", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NO THREATS LOGGED",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonEmerald,
                    letterSpacing = 1.sp
                )
            )
            Text(
                text = "\"$query\" is not currently flagged. Proceed with extreme caution.",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate(ROUTE_NEW_REPORT) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlertCoral),
                modifier = Modifier.height(50.dp).fillMaxWidth(0.8f)
            ) {
                Text("REPORT AS SUSPICIOUS", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }
    }
}