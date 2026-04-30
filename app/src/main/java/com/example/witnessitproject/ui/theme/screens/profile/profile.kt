package com.example.witnessitproject.ui.theme.screens.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.witnessitproject.data.ReportViewModel
import com.example.witnessitproject.ui.theme.data.AuthViewModel
import com.example.witnessitproject.ui.theme.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// ── Enhanced WitnessIt Tech Theme ───────────────────────────
private val DarkBg      = Color(0xFF05070A)
private val CardBg      = Color(0xFF0D1321)
private val Border      = Color(0xFF1E2D5A)
private val Accent      = Color(0xFFFF3D00) // Safety Orange
private val NeonCyan    = Color(0xFF00E5FF) // Tech Blue
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val reportViewModel: ReportViewModel = viewModel()

    val currentUser = FirebaseAuth.getInstance().currentUser
    var username by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        reportViewModel.fetchMyReports(context)
        currentUser?.uid?.let { uid ->
            FirebaseDatabase.getInstance()
                .getReference("User/$uid")
                .get()
                .addOnSuccessListener { snapshot ->
                    username = snapshot.child("username").value?.toString() ?: ""
                    phonenumber = snapshot.child("phonenumber").value?.toString() ?: ""
                }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("TERMINATE SESSION?", color = Color.White, fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to disconnect from the secure network?", color = TextMuted) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        navController.navigate(ROUTE_LOGIN) { popUpTo(0) { inclusive = true } }
                    }
                ) { Text("TERMINATE", color = Accent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("CANCEL", color = TextMuted) }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- VISUAL LAYER: Background Tech Mesh ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Accent.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.4f),
                    radius = 900f
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top Bar ──────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = NeonCyan)
                    }
                    Text(
                        text = "OPERATOR PROFILE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                IconButton(onClick = { showLogoutDialog = true }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = Accent)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Avatar Card (Digital ID Style) ─────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Accent.copy(0.2f), Color.Transparent))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (username.isNotBlank()) username.first().uppercase() else "?",
                                style = TextStyle(
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Accent,
                                    shadow = Shadow(color = Accent, blurRadius = 15f)
                                )
                            )
                        }

                        Text(
                            text = if (username.isNotBlank()) username.uppercase() else "INITIALIZING...",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Accent.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(0.3f))
                        ) {
                            Text(
                                text = "🛡 VERIFIED OPERATOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Accent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // ── Stats row (Telemetry Style) ─────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatItem(Modifier.weight(1f), "${reportViewModel.myReports.size}", "LOGS", NeonCyan)
                    StatItem(Modifier.weight(1f), "${reportViewModel.myReports.sumOf { it.upvotes }}", "FLAGS", Accent)
                    StatItem(Modifier.weight(1f), "${reportViewModel.myReports.count { it.verified }}", "CLEAR", Color.Green)
                }

                // ── Account info ──────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.6f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "SYSTEM CREDENTIALS",
                            fontSize = 10.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        ProfileInfoRow(Icons.Default.Person, "CODENAME", username.ifBlank { "—" })
                        HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 12.dp))
                        ProfileInfoRow(Icons.Default.Email, "NETWORK ID", currentUser?.email ?: "—")
                        HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 12.dp))
                        ProfileInfoRow(Icons.Default.Phone, "COMMS LINE", phonenumber.ifBlank { "—" })
                    }
                }

                // ── Logout button ─────────────────────────
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("DISCONNECT SESSION", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(icon, null, tint = TextDim, modifier = Modifier.size(20.dp))
        Column {
            Text(label, fontSize = 9.sp, color = TextDim, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(value, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatItem(modifier: Modifier = Modifier, value: String, label: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = color,
                    shadow = Shadow(color = color.copy(0.4f), blurRadius = 8f)
                )
            )
            Text(label, fontSize = 9.sp, color = TextDim, fontWeight = FontWeight.Bold)
        }
    }
}