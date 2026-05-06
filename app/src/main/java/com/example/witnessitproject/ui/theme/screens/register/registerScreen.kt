package com.example.witnessitproject.ui.theme.screens.register

import com.example.witnessitproject.ui.theme.data.AuthViewModel
import com.example.witnessitproject.ui.theme.navigation.ROUTE_LOGIN
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.witnessitproject.R

// ── WitnessIt Vibrant Theme Colors ─────────────────────
val DeepSpace    = Color(0xFF020617) // Background
val ElectricBlue = Color(0xFF6366F1) // Primary Action
val NeonEmerald  = Color(0xFF10B981) // Safety/Success
val SoftCoral    = Color(0xFFFB7185) // Danger/Accent
val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val TextMuted  = Color(0xFF94A3B8)
private val TextDim    = Color(0xFF64748B)

// ── Register Screen ────────────────────────────────────
@Composable
fun RegisterScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmpassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPwVisible by remember { mutableStateOf(false) }
    var phonenumber by remember { mutableStateOf("") }

    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Vibrant background with "lurking" color
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            DeepSpace,
            Color(0xFF1E1B4B), // Very deep Indigo "lurk"
            DeepSpace
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            // ── Shield Logo with Glow ────────────────────
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue.copy(alpha = 0.15f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Use Emerald for safety vibe
                Text("🛡️", fontSize = 42.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = "Join WitnessIt — report and verify with confidence",
                fontSize = 14.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // ── Glassmorphism Card ─────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardGlass),
                border = BorderStroke(1.dp, BorderGlass)
            ) {

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    ParkField(
                        label = "USERNAME",
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Enter username",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = ElectricBlue) }
                    )

                    ParkField(
                        label = "EMAIL",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter email address",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = ElectricBlue) }
                    )

                    ParkField(
                        label = "PHONE NUMBER",
                        value = phonenumber,
                        onValueChange = { phonenumber = it },
                        placeholder = "Enter your phone number",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = ElectricBlue) }
                    )

                    ParkField(
                        label = "PASSWORD",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Create a password",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = ElectricBlue) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = TextDim
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    ParkField(
                        label = "CONFIRM PASSWORD",
                        value = confirmpassword,
                        onValueChange = { confirmpassword = it },
                        placeholder = "Repeat your password",
                        leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = ElectricBlue) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPwVisible = !confirmPwVisible }) {
                                Icon(
                                    imageVector = if (confirmPwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = TextDim
                                )
                            }
                        },
                        visualTransformation = if (confirmPwVisible) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ── Primary Action Button ───────────
                    Button(
                        onClick = {
                            authViewModel.signup(username, email, phonenumber, password, confirmpassword, navController, context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            // Adding elevation for that "Vibrant" pop
                        )
                    ) {
                        Text("Secure Registration", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // ── Divider ─────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGlass)
                        Text("  OR  ", color = TextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderGlass)
                    }

                    // ── Google Sign-In ──────────────────
                    OutlinedButton(
                        onClick = {
                            authViewModel.signInWithGoogle(context, navController, scope)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGlass)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Continue with Google", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }

                    // ── Login Link ──────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already registered? ", color = TextMuted)
                        Text(
                            "Login here",
                            color = NeonEmerald, // Using safety green as a link color
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(ROUTE_LOGIN)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Vibrant Input Field ────────────────────────────────
@Composable
fun ParkField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextDim) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = BorderGlass,
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
                cursorColor = NeonEmerald,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}