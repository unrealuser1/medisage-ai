package com.unreal.medisageai.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unreal.medisageai.data.ChatMessage
import com.unreal.medisageai.data.ChatResponse
import com.unreal.medisageai.data.MediSageRepository
import com.unreal.medisageai.data.Sender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Opening line from Dr. MediSage shown on every fresh chat (matches Screen3.0). */
const val DR_GREETING: String =
    "Hello, I am Dr. MediSage, your AI medical assistant. How can I assist with " +
        "your clinical consultation or patient data today?"

private fun greetingMessage() = ChatMessage(text = DR_GREETING, sender = Sender.AI)

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(greetingMessage()),
    val isStreaming: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: MediSageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    /** Clears the conversation back to a blank instance (just the greeting). Powers "+ New Chat". */
    fun startNewChat() {
        _uiState.value = ChatUiState()
    }

    /** Loads a (mocked) history transcript into the chat, replacing the current conversation. */
    fun loadSession(messages: List<ChatMessage>) {
        _uiState.value = ChatUiState(
            messages = messages.ifEmpty { listOf(greetingMessage()) },
        )
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _uiState.value.isStreaming) return

        val userMessage = ChatMessage(text = trimmed, sender = Sender.USER)
        val pendingAiMessage = ChatMessage(text = "", sender = Sender.AI)

        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage + pendingAiMessage,
                isStreaming = true,
                error = null,
            )
        }
        val pendingAiId = pendingAiMessage.id

        viewModelScope.launch {
            repository.sendPrompt(trimmed).collect { response ->
                when (response) {
                    is ChatResponse.Loading -> Unit
                    is ChatResponse.Streaming -> updateAiMessage(pendingAiId) {
                        it.copy(text = response.partialText)
                    }
                    is ChatResponse.Success -> {
                        updateAiMessage(pendingAiId) {
                            it.copy(text = response.message.text, timestamp = response.message.timestamp)
                        }
                        _uiState.update { it.copy(isStreaming = false) }
                    }
                    is ChatResponse.Error -> _uiState.update { state ->
                        state.copy(
                            messages = state.messages.filterNot { it.id == pendingAiId },
                            isStreaming = false,
                            error = response.message,
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private inline fun updateAiMessage(id: String, transform: (ChatMessage) -> ChatMessage) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { if (it.id == id) transform(it) else it },
            )
        }
    }
}
