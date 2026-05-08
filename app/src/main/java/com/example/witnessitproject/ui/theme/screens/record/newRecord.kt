package com.example.witnessitproject.ui.theme.screens.record

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.witnessitproject.data.ReportViewModel


private val DeepSpace    = Color(0xFF020617)
private val CardGlass    = Color(0xFF0F172A).copy(alpha = 0.9f)
private val BorderGlass  = Color(0xFF334155).copy(alpha = 0.5f)

private val ElectricBlue = Color(0xFF6366F1)
private val AlertCoral   = Color(0xFFFB7185)
private val TextMuted   = Color(0xFF94A3B8)
private val TextDim     = Color(0xFF475569)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecordScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val viewModel: ReportViewModel = viewModel()

    var scamType by remember { mutableStateOf("M-Pesa") }
    var target by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val scamTypes = listOf("M-Pesa", "Phone", "Website", "Email", "Other")

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImages = (selectedImages + uris).distinct().take(5)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.9f),
                    radius = 900f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            Text(
                text = "SUBMIT INTEL",
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    shadow = Shadow(color = AlertCoral.copy(0.4f), blurRadius = 15f)
                )
            )
            Text(
                text = "ENCRYPTED CHANNEL 02 // SECURE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = ElectricBlue,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionLabel("INCIDENT CATEGORY")
            LazyRow(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(scamTypes) { type ->
                    val isSelected = scamType == type
                    FilterChip(
                        selected = isSelected,
                        enabled = true,
                        onClick = { scamType = type },
                        label = {
                            Text(
                                text = type.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CardGlass,
                            labelColor = TextMuted,
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BorderGlass,
                            selectedBorderColor = ElectricBlue,
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            SectionLabel("TARGET IDENTIFIER (PHONE / URL)")
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                placeholder = { Text("e.g. 0712XXXXXX", color = TextDim, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = BorderGlass,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardGlass,
                    unfocusedContainerColor = CardGlass
                )
            )

            Spacer(modifier = Modifier.height(24.dp))


            SectionLabel("INCIDENT DESCRIPTION")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Provide specific details of the threat...", color = TextDim, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = BorderGlass,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardGlass,
                    unfocusedContainerColor = CardGlass
                )
            )

            Spacer(modifier = Modifier.height(24.dp))


            SectionLabel("EVIDENCE ATTACHMENTS (${selectedImages.size}/5)")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardGlass)
                            .border(BorderStroke(1.dp, if(selectedImages.size < 5) ElectricBlue.copy(0.4f) else BorderGlass), RoundedCornerShape(20.dp))
                            .clickable { if (selectedImages.size < 5) galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, "Add", tint = if(selectedImages.size < 5) ElectricBlue else TextDim)
                            Text("ADD", fontSize = 10.sp, fontWeight = FontWeight.Black, color = if(selectedImages.size < 5) ElectricBlue else TextDim)
                        }
                    }
                }

                items(selectedImages) { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Evidence",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, BorderGlass, RoundedCornerShape(20.dp))
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(24.dp)
                                .clickable { selectedImages = selectedImages.filter { it != uri } },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))


            Button(
                onClick = {
                    if (target.isNotBlank() && description.isNotBlank()) {
                        viewModel.submitReport(selectedImages, scamType, target, description, context, navController)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = target.isNotBlank() && description.isNotBlank(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AlertCoral,
                    disabledContainerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                Text(
                    "TRANSMIT INTEL",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = if (target.isNotBlank() && description.isNotBlank()) Color.White else TextDim
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = TextMuted,
        letterSpacing = 1.2.sp
    )
}