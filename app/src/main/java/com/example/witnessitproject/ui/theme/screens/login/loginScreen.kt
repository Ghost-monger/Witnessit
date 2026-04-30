package com.example.witnessitproject.ui.theme.screens.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.witnessitproject.ui.theme.data.AuthViewModel
import com.example.witnessitproject.ui.theme.navigation.ROUTE_REGISTER

// ── Enhanced WitnessIt / FakeAlert KE Tech Theme ───────────────────────────
private val DarkBg      = Color(0xFF05070A) // Deep Tech Black
private val CardBg      = Color(0xFF0D1321) // Navy-tinged Slate
private val Border      = Color(0xFF1E2D5A) // Structural Blue
private val Accent      = Color(0xFFFF3D00) // Vibrant Safety Orange/Red
private val NeonCyan    = Color(0xFF00E5FF) // Techy Cyber Blue
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Background Glow for that "Interactive" feel
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Accent.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = 1000f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // App icon with glowing border
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(CardBg, Border))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🚨", fontSize = 38.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App name with techy shadow
            Text(
                text = "WITNESS IT KE",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    shadow = Shadow(color = Accent.copy(alpha = 0.5f), blurRadius = 15f)
                )
            )

            Text(
                text = "COMMUNITY INTELLIGENCE SYSTEM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Text(
                text = "🔒 Secure database connection active",
                fontSize = 11.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Login card - Glassmorphism style
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.9f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {

                    // Email field
                    Column {
                        Text(
                            text = "OPERATOR ID",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = {
                                Text("email@witnessit.ke", color = TextDim, fontSize = 14.sp)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Email, null, tint = Accent.copy(alpha = 0.8f))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Border,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Accent
                            )
                        )
                    }

                    // Password field
                    Column {
                        Text(
                            text = "ACCESS KEY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = {
                                Text("••••••••", color = TextDim, fontSize = 14.sp)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, null, tint = Accent.copy(alpha = 0.8f))
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.VisibilityOff
                                        else
                                            Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = TextDim
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Border,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Accent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Login button — tech refined
                    Button(
                        onClick = {
                            authViewModel.login(
                                email = email,
                                password = password,
                                navController = navController,
                                context = context
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "INITIALIZE SESSION",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register link
            Row {
                Text(
                    text = "New operator? ",
                    color = TextDim,
                    fontSize = 13.sp
                )
                Text(
                    text = "Request Access",
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(ROUTE_REGISTER)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}