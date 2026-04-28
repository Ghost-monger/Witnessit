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


// ── WitnessIt Theme Colors ─────────────────────────────
val NavyDark   = Color(0xFF020617)
private val NavyMid    = Color(0xFF0F172A)
private val NavyCard   = Color(0xFF1E293B)
private val NavyBorder = Color(0xFF334155)

val AccentRed  = Color(0xFFE11D48)

private val TextMuted  = Color(0xFF94A3B8)
val TextDim    = Color(0xFF64748B)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDark),
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

            // ── Logo ────────────────────────────
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(NavyCard),
                contentAlignment = Alignment.Center
            ) {
                Text("🛡️", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Join WitnessIt — report and verify with confidence",
                fontSize = 13.sp,
                color = TextDim,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            // ── Card ────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyMid),
                border = BorderStroke(1.dp, NavyBorder)
            ) {

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    ParkField(
                        label = "USERNAME",
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Enter username",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = TextDim) }
                    )

                    ParkField(
                        label = "EMAIL",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter email address",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = TextDim) }
                    )

                    ParkField(
                        label = "PHONE NUMBER",
                        value = phonenumber,
                        onValueChange = { phonenumber = it },
                        placeholder = "Enter your phone number",
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = TextDim) }
                    )

                    ParkField(
                        label = "PASSWORD",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Create a password",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextDim) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = TextDim
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else PasswordVisualTransformation()
                    )

                    ParkField(
                        label = "CONFIRM PASSWORD",
                        value = confirmpassword,
                        onValueChange = { confirmpassword = it },
                        placeholder = "Repeat your password",
                        leadingIcon = { Icon(Icons.Default.Check, null, tint = TextDim) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPwVisible = !confirmPwVisible }) {
                                Icon(
                                    imageVector = if (confirmPwVisible)
                                        Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = TextDim
                                )
                            }
                        },
                        visualTransformation = if (confirmPwVisible)
                            VisualTransformation.None
                        else PasswordVisualTransformation()
                    )

                    // ── Register Button ─────────────────
                    Button(
                        onClick = {
                            authViewModel.signup(
                                username,
                                email,
                                phonenumber,
                                password,
                                confirmpassword,
                                navController,
                                context
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentRed,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Create Account", fontWeight = FontWeight.Bold)
                    }

                    // ── Divider ─────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = NavyBorder)
                        Text("  OR  ", color = TextDim, fontSize = 12.sp)
                        Divider(modifier = Modifier.weight(1f), color = NavyBorder)
                    }

                    // ── Google Sign-In ──────────────────
                    OutlinedButton(
                        onClick = {
                            // TODO: Google Sign-In Logic
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Continue with Google", color = Color.White)
                        }
                    }

                    // ── Login Link ──────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already registered? ", color = TextDim)
                        Text(
                            "Login here",
                            color = AccentRed,
                            fontWeight = FontWeight.SemiBold,
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

// ── Reusable Field ─────────────────────────────────────
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
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
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
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = NavyBorder,
                focusedContainerColor = NavyCard,
                unfocusedContainerColor = NavyCard,
                cursorColor = AccentRed,
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