package by.voiteshonok.valacugi.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.domain.Message
import by.voiteshonok.valacugi.domain.MessagesRepository
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.domain.ThreadsRepository
import by.voiteshonok.valacugi.ui.directory.ThreadDisplayFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val threadId: String,
    private val threadsRepository: ThreadsRepository,
    private val messagesRepository: MessagesRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        observeChat()
    }

    fun sendMessage(body: String) {
        val senderId: String = _uiState.value.currentUserId ?: return
        val trimmedBody: String = body.trim()
        if (trimmedBody.isEmpty() || !_uiState.value.canSendMessage) {
            return
        }
        viewModelScope.launch {
            _uiState.update { previousState ->
                previousState.copy(isSendingMessage = true)
            }
            messagesRepository.sendMessage(
                threadId = threadId,
                senderId = senderId,
                body = trimmedBody
            )
            _uiState.update { previousState ->
                previousState.copy(isSendingMessage = false)
            }
        }
    }

    private fun observeChat() {
        viewModelScope.launch {
            combine(
                sessionRepository.observeSession().map { session -> session?.identification },
                threadsRepository.observeThread(threadId = threadId),
                messagesRepository.observeMessages(threadId = threadId)
            ) { currentUserId: String?, thread: MessageThread?, messages: List<Message> ->
                buildUiState(
                    currentUserId = currentUserId,
                    thread = thread,
                    messages = messages
                )
            }.collect { nextState: ChatUiState ->
                _uiState.update { previousState ->
                    nextState.copy(isSendingMessage = previousState.isSendingMessage)
                }
                val currentUserId: String? = nextState.currentUserId
                if (!currentUserId.isNullOrBlank() && nextState.messages.isNotEmpty()) {
                    launch {
                        messagesRepository.markThreadAsRead(threadId = threadId, userId = currentUserId)
                    }
                }
            }
        }
    }

    private fun buildUiState(
        currentUserId: String?,
        thread: MessageThread?,
        messages: List<Message>
    ): ChatUiState {
        val headerTitle: String = if (thread == null) {
            threadId.uppercase()
        } else {
            ThreadDisplayFormatter.formatChatHeaderTitle(threadTitle = thread.title)
        }
        val messageItems: List<ChatMessageItem> = messages.map { message: Message ->
            ChatMessageItem(
                id = message.id,
                senderLabel = MessageDisplayFormatter.formatSenderLabel(
                    senderId = message.senderId,
                    currentUserId = currentUserId
                ),
                body = message.body,
                sentAtLabel = MessageDisplayFormatter.formatSentAt(isoDateTime = message.sentAt),
                isFromCurrentUser = !currentUserId.isNullOrBlank() && message.senderId == currentUserId
            )
        }
        return ChatUiState(
            headerTitle = headerTitle,
            isLoading = false,
            currentUserId = currentUserId,
            messages = messageItems
        )
    }
}

class ChatViewModelFactory(
    private val threadId: String,
    private val threadsRepository: ThreadsRepository,
    private val messagesRepository: MessagesRepository,
    private val sessionRepository: SessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                threadId = threadId,
                threadsRepository = threadsRepository,
                messagesRepository = messagesRepository,
                sessionRepository = sessionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
