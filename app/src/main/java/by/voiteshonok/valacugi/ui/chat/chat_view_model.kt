package by.voiteshonok.valacugi.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.domain.ThreadsRepository
import by.voiteshonok.valacugi.ui.directory.ThreadDisplayFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val threadId: String,
    private val threadsRepository: ThreadsRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeThread()
    }

    private fun observeThread() {
        viewModelScope.launch {
            threadsRepository.observeThread(threadId = threadId).collect { thread: MessageThread? ->
                val headerTitle: String = if (thread == null) {
                    threadId.uppercase()
                } else {
                    ThreadDisplayFormatter.formatChatHeaderTitle(threadTitle = thread.title)
                }
                _uiState.update { previousState ->
                    previousState.copy(
                        isLoading = false,
                        headerTitle = headerTitle
                    )
                }
            }
        }
    }
}

class ChatViewModelFactory(
    private val threadId: String,
    private val threadsRepository: ThreadsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(threadId = threadId, threadsRepository = threadsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
