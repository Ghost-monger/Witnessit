package com.example.witnessitproject.ui.theme.screens.myreport

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF64748B)

@Composable
fun MyReportsScreen(navController: NavController) {
    val viewModel: ReportViewModel = viewModel()
    val context = LocalContext.current
    val myReports = viewModel.myReports

    LaunchedEffect(Unit) {
        viewModel.fetchMyReports(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = 800f
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(CardGlass, CircleShape).border(1.dp, BorderGlass, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ElectricBlue
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "DATA ARCHIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricBlue,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "My Reports",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                if (myReports.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AlertCoral.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertCoral.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${myReports.size} ENTRIES",
                            fontSize = 10.sp,
                            color = AlertCoral,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }


            if (myReports.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${myReports.size}",
                        label = "LOGGED",
                        glowColor = ElectricBlue
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${myReports.sumOf { it.upvotes }}",
                        label = "FLAGS",
                        glowColor = AlertCoral
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${myReports.count { it.verified }}",
                        label = "VERIFIED",
                        glowColor = NeonEmerald
                    )
                }
            }


            when {
                myReports.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text("📡", fontSize = 64.sp)
                            Text(
                                text = "LOGS EMPTY",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                            )
                            Text(
                                text = "Your personal threat intelligence log is empty. Secure the community by reporting suspicious activity.",
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.navigate(ROUTE_NEW_REPORT) },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AlertCoral),
                                modifier = Modifier.height(50.dp).fillMaxWidth(0.7f)
                            ) {
                                Text("SUBMIT INTEL", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
                    ) {
                        items(
                            items = myReports,
                            key = { it.reportId }
                        ) { report ->
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
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    glowColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlass),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGlass)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = glowColor,
                    shadow = Shadow(color = glowColor.copy(alpha = 0.5f), blurRadius = 12f)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = TextDim,
                letterSpacing = 1.5.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}