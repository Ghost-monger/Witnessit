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
    private val _pendingReports = mutableStateListOf<ReportModel>()

    val pendingReports: List<ReportModel> = _pendingReports

    private val _myReports = mutableStateListOf<ReportModel>()
    val myReports: List<ReportModel> = _myReports


    private val _processingUpvotes = mutableSetOf<String>()


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


                val userId = auth.currentUser?.uid ?: return@launch
                val report = hashMapOf(
                    "scamType" to scamType,
                    "target" to target,
                    "description" to description,
                    "evidenceUrls" to imageUrls,
                    "upvotes" to 0,
                    "reportedBy" to userId,
                    "timestamp" to Timestamp.now(),
                    "verified" to false,
                    "status" to "pending"
                )


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


    fun fetchReports(context: Context) {
        firestore.collection("reports")
            .whereEqualTo("status", "approved")
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


    fun fetchMyReports(context: Context) {
        val userId = auth.currentUser?.uid


        Toast.makeText(context, "Fetching reports for: $userId", Toast.LENGTH_LONG).show()

        if (userId == null) {
            Toast.makeText(context, "ERROR: userId is null", Toast.LENGTH_LONG).show()
            return
        }

        firestore.collection("reports")
            .whereEqualTo("reportedBy", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->

                Toast.makeText(context, "Found ${snapshot.documents.size} reports", Toast.LENGTH_LONG).show()
                _myReports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(ReportModel::class.java)
                    report?.let {
                        it.reportId = doc.id
                        _myReports.add(it)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                android.util.Log.e("MyReports", "Error: ${e.message}", e)
            }
    }


    fun upvoteReport(reportId: String, context: Context) {
        val userId = auth.currentUser?.uid ?: return


        if (_processingUpvotes.contains(reportId)) return
        _processingUpvotes.add(reportId)

        val upvoteRef = firestore
            .collection("reports")
            .document(reportId)
            .collection("upvotes")
            .document(userId)

        upvoteRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {

                    upvoteRef.delete()
                        .addOnSuccessListener {
                            firestore.collection("reports")
                                .document(reportId)
                                .update("upvotes", FieldValue.increment(-1))
                                .addOnSuccessListener {

                                    updateLocalUpvoteCount(reportId, -1)
                                    _upvotedReportIds.remove(reportId)
                                    _processingUpvotes.remove(reportId)
                                }
                                .addOnFailureListener {
                                    _processingUpvotes.remove(reportId)
                                    Toast.makeText(context, "Flag failed", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            _processingUpvotes.remove(reportId)
                        }
                } else {

                    upvoteRef.set(
                        mapOf(
                            "userId" to userId,
                            "timestamp" to Timestamp.now()
                        )
                    )
                        .addOnSuccessListener {
                            firestore.collection("reports")
                                .document(reportId)
                                .update("upvotes", FieldValue.increment(1))
                                .addOnSuccessListener {

                                    updateLocalUpvoteCount(reportId, 1)
                                    _upvotedReportIds.add(reportId)
                                    _processingUpvotes.remove(reportId)
                                }
                                .addOnFailureListener {

                                    upvoteRef.delete()
                                    _processingUpvotes.remove(reportId)
                                    Toast.makeText(context, "Flag failed", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            _processingUpvotes.remove(reportId)
                            Toast.makeText(context, "Flag failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                _processingUpvotes.remove(reportId)
                Toast.makeText(context, "Flag failed", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateLocalUpvoteCount(reportId: String, delta: Int) {
        val reportIndex = _reports.indexOfFirst { it.reportId == reportId }
        if (reportIndex != -1) {
            _reports[reportIndex] = _reports[reportIndex].copy(
                upvotes = _reports[reportIndex].upvotes + delta
            )
        }
        val myIndex = _myReports.indexOfFirst { it.reportId == reportId }
        if (myIndex != -1) {
            _myReports[myIndex] = _myReports[myIndex].copy(
                upvotes = _myReports[myIndex].upvotes + delta
            )
        }
        val highIndex = _highPriorityReports.indexOfFirst { it.reportId == reportId }
        if (highIndex != -1) {
            _highPriorityReports[highIndex] = _highPriorityReports[highIndex].copy(
                upvotes = _highPriorityReports[highIndex].upvotes + delta
            )
        }
    }



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
    fun updateReport(
        reportId: String,
        scamType: String,
        target: String,
        description: String,
        imageUris: List<Uri>,
        existingUrls: List<String>,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val newImageUrls = mutableListOf<String>()
                imageUris.forEachIndexed { index, uri ->
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Uploading image ${index + 1} of ${imageUris.size}...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val url = uploadToCloudinary(context, uri)
                    newImageUrls.add(url)
                }


                val allImageUrls = existingUrls + newImageUrls


                val updates = mapOf(
                    "scamType" to scamType,
                    "target" to target,
                    "description" to description,
                    "evidenceUrls" to allImageUrls
                )


                withContext(Dispatchers.Main) {
                    firestore.collection("reports")
                        .document(reportId)
                        .update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Report updated successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            fetchReports(context)
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Update failed: ${e.message}",
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

    fun fetchPendingReports(context: Context) {
        firestore.collection("reports")
            .whereEqualTo("status", "pending")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                _pendingReports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(ReportModel::class.java)
                    report?.let {
                        it.reportId = doc.id
                        _pendingReports.add(it)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun approveReport(reportId: String, context: Context) {
        firestore.collection("reports")
            .document(reportId)
            .update(
                mapOf(
                    "status" to "approved",
                    "verified" to true
                )
            )
            .addOnSuccessListener {
                _pendingReports.removeIf { it.reportId == reportId }
                Toast.makeText(context, "Report approved ✅", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to approve", Toast.LENGTH_SHORT).show()
            }
    }


    fun rejectReport(reportId: String, context: Context) {
        firestore.collection("reports")
            .document(reportId)
            .delete()
            .addOnSuccessListener {
                _pendingReports.removeIf { it.reportId == reportId }
                Toast.makeText(context, "Report rejected ❌", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to reject", Toast.LENGTH_SHORT).show()
            }
    }
    private val _highPriorityReports = mutableStateListOf<ReportModel>()
    val highPriorityReports: List<ReportModel> = _highPriorityReports

    fun fetchHighPriorityReports(context: Context) {
        firestore.collection("reports")
            .whereEqualTo("status", "approved")
            .whereGreaterThanOrEqualTo("upvotes", 20)
            .orderBy("upvotes", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                _highPriorityReports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(ReportModel::class.java)
                    report?.let {
                        it.reportId = doc.id
                        _highPriorityReports.add(it)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Failed to load high priority reports: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private val _upvotedReportIds = mutableStateListOf<String>()
    val upvotedReportIds: List<String> = _upvotedReportIds


    fun fetchUserUpvotes(context: Context) {
        val userId = auth.currentUser?.uid ?: return



        firestore.collection("reports")
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val reportId = doc.id
                    firestore.collection("reports")
                        .document(reportId)
                        .collection("upvotes")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { upvoteDoc ->
                            if (upvoteDoc.exists()) {
                                if (!_upvotedReportIds.contains(reportId)) {
                                    _upvotedReportIds.add(reportId)
                                }
                            }
                        }
                }
            }
            .addOnFailureListener {

            }
    }
}

