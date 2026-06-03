package by.voiteshonok.valacugi.ui.directory

import by.voiteshonok.valacugi.domain.MessageThread

data class DirectoryUiState(
    val threads: List<MessageThread> = emptyList()
)
