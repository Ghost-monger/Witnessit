package com.example.witnessitproject.ui.theme.screens.splashscreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.witnessitproject.ui.theme.navigation.ROUTE_DASHBOARD
import com.example.witnessitproject.ui.theme.navigation.ROUTE_SPLASH_SCREEN
import kotlinx.coroutines.delay

// ── Enhanced WitnessIt Tech Theme ───────────────────────────
private val DarkBg      = Color(0xFF05070A)
private val Accent      = Color(0xFFFF3D00) // Safety Orange
private val NeonCyan    = Color(0xFF00E5FF) // Tech Blue
private val TextMuted   = Color(0xFF94A3B8)

@Composable
fun SplashScreen(navController: NavController) {

    // Animations
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    LaunchedEffect(Unit) {
        // Pop-in effect
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        alpha.animateTo(targetValue = 1f, animationSpec = tween(800))
        textAlpha.animateTo(targetValue = 1f, animationSpec = tween(1000))

        delay(2500L) // Wait for boot-up sequence

        navController.navigate(ROUTE_DASHBOARD) {
            popUpTo(ROUTE_SPLASH_SCREEN) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        // Background mesh glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Accent.copy(alpha = 0.07f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    radius = 800f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Icon with pulsing outer ring
            Box(contentAlignment = Alignment.Center) {
                // Animated outer glow ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulseScale)
                        .alpha(alpha.value * 0.3f)
                        .background(Accent.copy(alpha = 0.2f), CircleShape)
                )

                // Main Shield Icon
                Box(
                    modifier = Modifier
                        .scale(scale.value)
                        .alpha(alpha.value)
                        .size(90.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0D1321), Color(0xFF1E2D5A))),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛡️", fontSize = 42.sp)
                }
            }

            Spacer(Modifier.height(32.dp))

            // App Name with Cyber Shadow
            Text(
                text = "WITNESS IT KE",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    shadow = Shadow(color = Accent.copy(alpha = 0.6f), blurRadius = 20f)
                ),
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            // Sub-header logic
            Text(
                text = "COMMUNITY THREAT INTELLIGENCE",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonCyan,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(60.dp))

            // Loading Bar (Tech Style)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(120.dp)
                        .height(2.dp)
                        .alpha(textAlpha.value),
                    color = Accent,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "INITIALIZING CORE...",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.alpha(textAlpha.value)
                )
            }
        }

        // Footer Tag
        Text(
            text = "SECURE NETWORK // ENCRYPTED ACCESS",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.3f),
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(textAlpha.value)
        )
    }
}