package com.unreal.medisageai.data

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: Sender,
    val timestamp: Long = System.currentTimeMillis(),
    /**
     * Optional follow-up suggestion chips rendered beneath an AI bubble
     * (e.g. "Medication History", "ECG Protocol"). Tapping one sends it as a prompt.
     */
    val suggestions: List<String> = emptyList(),
    /**
     * Optional pre-formatted clock label (e.g. "09:41 AM"). When null the UI formats
     * [timestamp] live. Used by mocked history transcripts to match the mockups exactly.
     */
    val timeLabel: String? = null,
)

enum class Sender { USER, AI }
