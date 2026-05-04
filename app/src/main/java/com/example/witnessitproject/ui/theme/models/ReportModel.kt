package com.example.witnessitproject.ui.theme.models

import com.google.firebase.Timestamp

data class ReportModel(
    var reportId: String = "",
    val scamType: String = "",
    val target: String = "",
    val description: String = "",
    val evidenceUrls: List<String> = emptyList(),
    val upvotes: Int = 0,
    val reportedBy: String = "",
    val timestamp: Timestamp? = null,
    val verified: Boolean = false,
    val status: String = "pending"
)
