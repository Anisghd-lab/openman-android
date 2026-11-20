package com.ghdinteractivestudio.openman.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class StepStatus {
    PENDING,
    ACTIVE,
    DONE,
    ERROR
}

@Serializable
enum class ToolType {
    BROWSER,
    TERMINAL,
    SEARCH,
    FILE_WRITER,
    PLANNER,
    REASONING
}

@Serializable
data class Step(
    val id: String,
    val taskId: String,
    val stepIndex: Int,
    val title: String,
    val description: String = "",
    val status: StepStatus = StepStatus.PENDING,
    val toolType: ToolType = ToolType.REASONING,
    val toolInput: String? = null,
    val toolOutput: String? = null,
    val thinking: String? = null,
    val durationMs: Long? = null
)
