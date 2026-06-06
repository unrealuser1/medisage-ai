package com.unreal.medisageai.data

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MediSageRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
) : MediSageRepository {

    override fun sendPrompt(prompt: String): Flow<ChatResponse> = flow {
        emit(ChatResponse.Loading)

        val buffer = StringBuilder()
        generativeModel.generateContentStream(prompt).collect { chunk ->
            chunk.text?.let { delta ->
                buffer.append(delta)
                emit(ChatResponse.Streaming(buffer.toString()))
            }
        }

        emit(
            ChatResponse.Success(
                ChatMessage(
                    text = buffer.toString(),
                    sender = Sender.AI,
                )
            )
        )
    }.catch { e ->
        emit(ChatResponse.Error(e.message ?: "Failed to reach Gemini", e))
    }
}
