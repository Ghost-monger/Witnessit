package com.example.witnessitproject.ui.theme.data


import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.witnessitproject.ui.theme.models.ReportModel
import com.example.witnessitproject.ui.theme.navigation.ROUTE_DASHBOARD
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class ReportViewModel : ViewModel() {

    private val cloudinaryUrl = "https://api.cloudinary.com/v1_1/dmds9mat3/image/upload"
    private val uploadPreset = "pic_folder"

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // List of all reports for the home feed
    private val _reports = mutableStateListOf<ReportModel>()
    val reports: List<ReportModel> = _reports

    // List of current user's reports for My Reports screen
    private val _myReports = mutableStateListOf<ReportModel>()
    val myReports: List<ReportModel> = _myReports

    // Submits a new scam report with multiple images
    fun submitReport(
        imageUris: List<Uri>,
        scamType: String,
        target: String,
        description: String,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Upload all images and collect their URLs
                val imageUrls = mutableListOf<String>()
                imageUris.forEachIndexed { index, uri ->
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Uploading image ${index + 1} of ${imageUris.size}...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val url = uploadToCloudinary(context, uri)
                    imageUrls.add(url)
                }

                // Build the report document
                val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")
                val report = mapOf(
                    "scamType" to scamType,
                    "target" to target,
                    "description" to description,
                    "evidenceUrls" to imageUrls,
                    "upvotes" to 0,
                    "reportedBy" to userId,
                    "timestamp" to Timestamp.now(),
                    "verified" to false
                )

                // Save to Firestore
                firestore.collection("reports").add(report).await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Report submitted successfully", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_DASHBOARD)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Fetch all reports for the home feed — ordered by newest first
    fun fetchReports(context: Context) {
        firestore.collection("reports")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                _reports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(ReportModel::class.java)
                    report?.let {
                        it.reportId = doc.id
                        _reports.add(it)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load reports", Toast.LENGTH_SHORT).show()
            }
    }

    // Fetch only the current user's reports for My Reports screen
    fun fetchMyReports(context: Context) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("reports")
            .whereEqualTo("reportedBy", userId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                _myReports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(ReportModel::class.java)
                    report?.let {
                        it.reportId = doc.id
                        _myReports.add(it)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load your reports", Toast.LENGTH_SHORT).show()
            }
    }

    // Upvote a report — prevents double voting using a subcollection
    fun upvoteReport(reportId: String, context: Context) {
        val userId = auth.currentUser?.uid ?: return
        val upvoteRef = firestore
            .collection("reports")
            .document(reportId)
            .collection("upvotes")
            .document(userId) // one doc per user — natural dedup

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = upvoteRef.get().await()
                if (existing.exists()) {
                    // Already upvoted — remove it (toggle off)
                    upvoteRef.delete().await()
                    firestore.collection("reports").document(reportId)
                        .update("upvotes", com.google.firebase.firestore.FieldValue.increment(-1))
                        .await()
                } else {
                    // New upvote
                    upvoteRef.set(mapOf("userId" to userId, "timestamp" to Timestamp.now())).await()
                    firestore.collection("reports").document(reportId)
                        .update("upvotes", com.google.firebase.firestore.FieldValue.increment(1))
                        .await()
                }
                fetchReports(context) // refresh feed
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Upvote failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Delete a report — only the owner can do this
    fun deleteReport(reportId: String, context: Context) {
        firestore.collection("reports")
            .document(reportId)
            .delete()
            .addOnSuccessListener {
                _reports.removeIf { it.reportId == reportId }
                _myReports.removeIf { it.reportId == reportId }
                Toast.makeText(context, "Report deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete report", Toast.LENGTH_SHORT).show()
            }
    }

    // Cloudinary upload — same pattern as your CarViewModel
    private fun uploadToCloudinary(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes() ?: throw Exception("Image read failed")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", "image.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .addFormDataPart("folder", "fakealert_ke") // neat folder in Cloudinary
            .build()

        val request = Request.Builder()
            .url(cloudinaryUrl)
            .post(requestBody)
            .build()

        val response = OkHttpClient().newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Upload failed: ${response.code}")

        val responseBody = response.body?.string()
        val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
            .find(responseBody ?: "")?.groupValues?.get(1)
            ?.replace("\\/", "/") // Cloudinary escapes slashes in JSON

        return secureUrl ?: throw Exception("Failed to get image URL")
    }
}