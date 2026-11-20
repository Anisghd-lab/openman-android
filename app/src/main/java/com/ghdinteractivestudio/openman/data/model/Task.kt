package com.ghdinteractivestudio.openman.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskMode {
    AGENT, // Full autonomous multi-step execution with tools & computer
    CHAT,  // Interactive direct conversational mode
    PLAN   // Strategic breakdown and execution roadmap
}

@Serializable
enum class TaskStatus {
    PENDING,
    PLANNING,
    RUNNING,
    WAITING_APPROVAL,
    COMPLETED,
    FAILED
}

@Serializable
data class Task(
    val id: String,
    val title: String,
    val prompt: String,
    val mode: TaskMode = TaskMode.AGENT,
    val status: TaskStatus = TaskStatus.PENDING,
    val modelUsed: String = "auto",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val summary: String? = null,
    val liveUrl: String? = null
)
