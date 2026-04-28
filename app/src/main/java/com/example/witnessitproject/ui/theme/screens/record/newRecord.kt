package com.example.witnessitproject.ui.theme.screens.record


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.UUID



@Composable
fun NewRecordScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Form fields
    var scamType by remember { mutableStateOf("M-Pesa") }
    var target by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Image state
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf("") }

    val scamTypes = listOf("M-Pesa", "Phone", "Website", "Email", "Other")

    // Gallery picker — allows multiple images
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        // Limit to 5 images max
        val combined = (selectedImages + uris).distinct().take(5)
        selectedImages = combined
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Report a scam", fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)

        Spacer(modifier = Modifier.height(16.dp))

        // Scam type selector
        Text("Scam type", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            scamTypes.forEach { type ->
                FilterChip(
                    selected = scamType == type,
                    onClick = { scamType = type },
                    label = { Text(type, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target input
        Text("Phone number / website / account", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = target,
            onValueChange = { target = it },
            placeholder = { Text("e.g. 0712345678 or scamsite.co.ke") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description input
        Text("What happened?", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Describe the scam in detail...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image section
        Text("Screenshots / evidence (max 5)", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Add button — always first
            item {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(1.dp, Color.LightGray),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (selectedImages.size < 5) {
                                galleryLauncher.launch("image/*")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = "Add image",
                            tint = Color.Gray)
                        Text("Add photo", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            // Selected images
            items(selectedImages) { uri ->
                Box(modifier = Modifier.size(90.dp)) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    // Remove button
                    IconButton(
                        onClick = {
                            selectedImages = selectedImages.filter { it != uri }
                        },
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("${selectedImages.size}/5 photos added", fontSize = 11.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Upload progress
        if (uploadProgress.isNotEmpty()) {
            Text(uploadProgress, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Submit button
        Button(
            onClick = {
                if (target.isBlank() || description.isBlank()) return@Button
                scope.launch {
                    submitReport(
                        scamType = scamType,
                        target = target,
                        description = description,
                        imageUris = selectedImages,
                        onProgress = { uploadProgress = it },
                        onSuccess = { navController.popBackStack() },
                        onError = { uploadProgress = "Error: $it" }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading && target.isNotBlank() && description.isNotBlank()
        ) {
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            } else {
                Text("Submit report")
            }
        }
    }
}

// Uploads images to Firebase Storage then saves report to Firestore
suspend fun submitReport(
    scamType: String,
    target: String,
    description: String,
    imageUris: List<Uri>,
    onProgress: (String) -> Unit,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val imageUrls = mutableListOf<String>()

    // Upload each image to Firebase Storage
    imageUris.forEachIndexed { index, uri ->
        onProgress("Uploading image ${index + 1} of ${imageUris.size}...")
        val filename = "reports/${userId}/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(filename)

        try {
            val uploadTask = ref.putFile(uri)
            // Wait for upload to complete
            val taskSnapshot = kotlinx.coroutines.tasks.await(uploadTask)
            val downloadUrl = kotlinx.coroutines.tasks.await(
                taskSnapshot.storage.downloadUrl
            )
            imageUrls.add(downloadUrl.toString())
        } catch (e: Exception) {
            onError(e.message ?: "Upload failed")
            return
        }
    }

    // Save report to Firestore
    onProgress("Saving report...")
    val report = hashMapOf(
        "scamType" to scamType,
        "target" to target,
        "description" to description,
        "evidenceUrls" to imageUrls,  // list of download URLs
        "upvotes" to 0,
        "reportedBy" to userId,
        "timestamp" to com.google.firebase.Timestamp.now(),
        "verified" to false
    )

    try {
        kotlinx.coroutines.tasks.await(
            firestore.collection("reports").add(report)
        )
        onProgress("")
        onSuccess()
    } catch (e: Exception) {
        onError(e.message ?: "Failed to save report")
    }
}