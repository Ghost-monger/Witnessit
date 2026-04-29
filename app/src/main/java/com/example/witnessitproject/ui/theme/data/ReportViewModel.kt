package com.example.witnessitproject.data

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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val _reports = mutableStateListOf<ReportModel>()
    val reports: List<ReportModel> = _reports

    private val _myReports = mutableStateListOf<ReportModel>()
    val myReports: List<ReportModel> = _myReports

    // Submit report — uploads images to Cloudinary then saves to Firestore
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
                // Upload all images first
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

                // Build report map
                val userId = auth.currentUser?.uid ?: return@launch
                val report = hashMapOf(
                    "scamType" to scamType,
                    "target" to target,
                    "description" to description,
                    "evidenceUrls" to imageUrls,
                    "upvotes" to 0,
                    "reportedBy" to userId,
                    "timestamp" to Timestamp.now(),
                    "verified" to false
                )

                // Save to Firestore using callbacks — no await needed
                withContext(Dispatchers.Main) {
                    firestore.collection("reports")
                        .add(report)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Report submitted successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(ROUTE_DASHBOARD)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Failed to save report: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Fetch all reports for home feed
    fun fetchReports(context: Context) {
        firestore.collection("reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
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

    // Fetch only current user's reports
    fun fetchMyReports(context: Context) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("reports")
            .whereEqualTo("reportedBy", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
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

    // Upvote toggle — no await, pure callbacks
    fun upvoteReport(reportId: String, context: Context) {
        val userId = auth.currentUser?.uid ?: return
        val upvoteRef = firestore
            .collection("reports")
            .document(reportId)
            .collection("upvotes")
            .document(userId)

        // Check if already upvoted
        upvoteRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Already upvoted — remove it
                    upvoteRef.delete()
                    firestore.collection("reports")
                        .document(reportId)
                        .update("upvotes", FieldValue.increment(-1))
                } else {
                    // New upvote — add it
                    upvoteRef.set(
                        mapOf(
                            "userId" to userId,
                            "timestamp" to Timestamp.now()
                        )
                    )
                    firestore.collection("reports")
                        .document(reportId)
                        .update("upvotes", FieldValue.increment(1))
                        .addOnSuccessListener {
                            fetchReports(context) // refresh feed
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Upvote failed", Toast.LENGTH_SHORT).show()
            }
    }

    // Delete a report
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
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }

    // Cloudinary upload — runs on IO thread, no await needed
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
            .addFormDataPart("folder", "fakealert_ke")
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
            ?.replace("\\/", "/")

        return secureUrl ?: throw Exception("Failed to get image URL")
    }
}