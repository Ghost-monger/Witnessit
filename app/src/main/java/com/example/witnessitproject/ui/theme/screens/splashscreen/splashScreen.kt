package com.example.witnessitproject.ui.theme.screens.splashscreen



import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.witnessitproject.ui.theme.navigation.ROUTE_DASHBOARD
import com.example.witnessitproject.ui.theme.navigation.ROUTE_SPLASH_SCREEN
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // Animations
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )

        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )

        delay(2200L)

        navController.navigate(ROUTE_DASHBOARD) {
            popUpTo(ROUTE_SPLASH_SCREEN) { inclusive = true }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // deep navy
                        Color(0xFF1E293B),
                        Color(0xFF020617)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Shield Icon
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E3A8A), // soft glow blue
                                Color(0xFF020617)
                            )
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛡️",
                    fontSize = 60.sp
                )
            }

            Spacer(Modifier.height(28.dp))

            // App Name
            Text(
                text = "WitnessIt",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(6.dp))

            // Tagline
            Text(
                text = "Proof protects you",
                fontSize = 14.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(Modifier.height(50.dp))

            // Loading
            CircularProgressIndicator(
                color = Color(0xFFE11D48), // subtle red accent
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(22.dp)
                    .alpha(textAlpha.value)
            )
        }

        // Bottom Tag
        Text(
            text = "Secure • Report • Verify",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
                .alpha(textAlpha.value)
        )
    }
}