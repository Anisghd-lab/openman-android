package com.ghdinteractivestudio.openman.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ArtifactType {
    MARKDOWN,
    CODE,
    WEBSITE_HTML,
    JSON_DATA
}

@Serializable
data class Artifact(
    val id: String,
    val taskId: String,
    val title: String,
    val type: ArtifactType,
    val content: String,
    val fileName: String,
    val language: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
