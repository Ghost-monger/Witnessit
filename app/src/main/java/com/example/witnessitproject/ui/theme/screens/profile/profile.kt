package com.example.witnessitproject.ui.theme.screens.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

// ── FakeAlert KE Theme ───────────────────────────
private val DarkBg    = Color(0xFF0A0F1E)
private val CardBg    = Color(0xFF131D3B)
private val Border    = Color(0xFF1E2D5A)
private val Accent    = Color(0xFF993C1D)
private val TextMuted = Color(0xFF7A8AB5)
private val TextDim   = Color(0xFF5A6A90)

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val reportViewModel: ReportViewModel = viewModel()

    // Get current Firebase Auth user
    val currentUser = FirebaseAuth.getInstance().currentUser

    // User data state — pulled from Realtime Database
    var username by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }

    // Logout dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Fetch user profile data from Firebase Realtime Database
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

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?", color = Color.White) },
            text = {
                Text(
                    "You will be returned to the login screen.",
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Log out", color = Accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {

        // ── Top bar ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // Logout icon button
            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = Accent
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ── Avatar + name ─────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Avatar circle with first letter of username
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Accent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (username.isNotBlank())
                                username.first().uppercaseChar().toString()
                            else "?",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Accent
                        )
                    }

                    // Username
                    Text(
                        text = if (username.isNotBlank()) username else "Loading...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Community member badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Accent.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "🛡 Community member",
                            fontSize = 12.sp,
                            color = Accent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── Stats row ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatItem(
                    modifier = Modifier.weight(1f),
                    value = "${reportViewModel.myReports.size}",
                    label = "Reports"
                )
                StatItem(
                    modifier = Modifier.weight(1f),
                    value = "${reportViewModel.myReports.sumOf { it.upvotes }}",
                    label = "Flags received"
                )
                StatItem(
                    modifier = Modifier.weight(1f),
                    value = "${reportViewModel.myReports.count { it.verified }}",
                    label = "Confirmed"
                )
            }

            // ── Account info ──────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = "ACCOUNT INFO",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Username",
                        value = username.ifBlank { "—" }
                    )

                    HorizontalDivider(
                        color = Border,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = currentUser?.email ?: "—"
                    )

                    HorizontalDivider(
                        color = Border,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = phonenumber.ifBlank { "—" }
                    )
                }
            }

            // ── Logout button ─────────────────────────
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Log out",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Single info row — icon + label + value
@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

// Stat item for profile stats row
@Composable
fun StatItem(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Accent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}