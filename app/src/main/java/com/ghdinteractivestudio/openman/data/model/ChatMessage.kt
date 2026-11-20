package com.ghdinteractivestudio.openman.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val taskId: String,
    val sender: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isArtifactUpdate: Boolean = false
)

@Serializable
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val isEnabled: Boolean = true,
    val iconEmoji: String = "⚡"
)
