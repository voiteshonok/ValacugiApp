package by.voiteshonok.valacugi.ui.chat

data class ChatMessageItem(
    val id: String,
    val senderLabel: String,
    val body: String,
    val sentAtLabel: String,
    val isFromCurrentUser: Boolean
)

data class ChatUiState(
    val headerTitle: String = "TRANSMISSION",
    val isLoading: Boolean = true,
    val currentUserId: String? = null,
    val messages: List<ChatMessageItem> = emptyList(),
    val isSendingMessage: Boolean = false
) {
    val canSendMessage: Boolean
        get() = !currentUserId.isNullOrBlank() && !isSendingMessage
}
