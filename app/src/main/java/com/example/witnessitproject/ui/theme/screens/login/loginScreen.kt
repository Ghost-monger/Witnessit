package com.example.witnessitproject.ui.theme.screens.login


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.witnessitproject.ui.theme.screens.register.ParkField

// ── Same colour tokens as Dashboard & Register ─────────────
private val NavyDark   = Color(0xFF0A0F1E)
private val NavyMid    = Color(0xFF0D1630)
private val NavyCard   = Color(0xFF131D3B)
private val NavyBorder = Color(0xFF1E2D5A)
private val Amber      = Color(0xFFF5C842)
private val BlueAccent = Color(0xFF7B9FFF)
private val TextMuted  = Color(0xFF7A8AB5)
val TextDim    = Color(0xFF5A6A90)

@Composable
fun LoginScreen(navController: NavController) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Brand header ───────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NavyCard),
                contentAlignment = Alignment.Center
            ) {
                Text("🅿", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ParkSmart",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Amber
            )
            Text(
                text = "Smart Parking Made Easy",
                fontSize = 12.sp,
                color = TextDim,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            // ── Login card ─────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyMid),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Email
                    ParkField(
                        label = "EMAIL",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "example@email.com",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = TextDim) }
                    )

                    // Password
                    ParkField(
                        label = "PASSWORD",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Enter your password",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextDim) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = TextDim
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation()
                    )

                    // Forgot password
                    Text(
                        text = "Forgot password?",
                        color = BlueAccent,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { /* TODO: forgot password */ }
                    )

                    // Login button
                    Button(
                        onClick = { authViewModel.login(
                            email=email,
                            password=password,
                            navController=navController,
                            context=context
                        )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Amber,
                            contentColor = NavyDark
                        )
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Register link ──────────────────────────
            Spacer(modifier = Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Don't have an account? ", color = TextDim, fontSize = 13.sp)
                Text(
                    text = "Register here",
                    color = Amber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
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