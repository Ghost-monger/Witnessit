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

// ── Unified WitnessIt Vibrant Theme ───────────────────────────
private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val ElectricBlue = Color(0xFF6366F1) // Primary Action/Trust
private val AlertCoral   = Color(0xFFFB7185) // Threat/Danger
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
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
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

        delay(2500L) // Boot-up delay

        navController.navigate(ROUTE_DASHBOARD) {
            popUpTo(ROUTE_SPLASH_SCREEN) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        // --- VISUAL LAYER: Background Core Pulse ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    radius = 900f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Icon with pulsing outer ring
            Box(contentAlignment = Alignment.Center) {
                // Animated outer glow ring (Coral Pulse)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .alpha(alpha.value * 0.2f)
                        .background(AlertCoral.copy(alpha = 0.3f), CircleShape)
                )

                // Main Shield Icon Container
                Box(
                    modifier = Modifier
                        .scale(scale.value)
                        .alpha(alpha.value)
                        .size(100.dp)
                        .background(
                            Brush.linearGradient(listOf(CardGlass, Color(0xFF1E293B))),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛡️", fontSize = 48.sp)
                }
            }

            Spacer(Modifier.height(32.dp))

            // App Name with Cyber Shadow
            Text(
                text = "WITNESS IT",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    shadow = Shadow(color = ElectricBlue.copy(alpha = 0.6f), blurRadius = 25f)
                ),
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(8.dp))

            // Tech Sub-header
            Text(
                text = "VIGILANT COMMUNITY NETWORK",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = ElectricBlue,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(64.dp))

            // Loading Bar (Tech Style)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.dp)
                        .alpha(textAlpha.value),
                    color = ElectricBlue,
                    trackColor = Color.White.copy(alpha = 0.05f)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "ESTABLISHING SECURE UPLINK...",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.alpha(textAlpha.value)
                )
            }
        }

        // Footer Metadata
        Text(
            text = "VERIFIED ACCESS // KORE DATA SYNC",
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White.copy(alpha = 0.2f),
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(textAlpha.value)
        )
    }
}