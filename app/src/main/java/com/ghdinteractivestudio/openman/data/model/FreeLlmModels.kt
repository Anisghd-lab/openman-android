package com.ghdinteractivestudio.openman.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreeLlmMessage(
    val role: String,
    val content: String
)

@Serializable
data class FreeLlmChatRequest(
    val model: String = "auto",
    val messages: List<FreeLlmMessage>,
    val temperature: Float = 0.7f,
    @SerialName("max_tokens") val maxTokens: Int = 4096,
    val stream: Boolean = false
)

@Serializable
data class FreeLlmChoice(
    val index: Int = 0,
    val message: FreeLlmMessage? = null,
    @SerialName("finish_reason") val finishReason: String? = null
)

@Serializable
data class FreeLlmUsage(
    @SerialName("prompt_tokens") val promptTokens: Int = 0,
    @SerialName("completion_tokens") val completionTokens: Int = 0,
    @SerialName("total_tokens") val totalTokens: Int = 0
)

@Serializable
data class FreeLlmChatResponse(
    val id: String = "",
    val model: String = "",
    val choices: List<FreeLlmChoice> = emptyList(),
    val usage: FreeLlmUsage? = null,
    val error: FreeLlmError? = null
)

@Serializable
data class FreeLlmError(
    val message: String,
    val type: String? = null
)

@Serializable
data class ModelItem(
    val id: String,
    val name: String? = null,
    @SerialName("context_window") val contextWindow: Int? = null,
    val available: Boolean = true
)

@Serializable
data class ModelsListResponse(
    val data: List<ModelItem> = emptyList()
)
