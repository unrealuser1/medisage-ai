package com.unreal.medisageai.data

sealed interface ChatResponse {
    data object Loading : ChatResponse
    data class Streaming(val partialText: String) : ChatResponse
    data class Success(val message: ChatMessage) : ChatResponse
    data class Error(val message: String, val cause: Throwable? = null) : ChatResponse
}
