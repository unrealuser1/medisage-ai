package com.unreal.medisageai.data

import kotlinx.coroutines.flow.Flow

interface MediSageRepository {
    fun sendPrompt(prompt: String): Flow<ChatResponse>
}
