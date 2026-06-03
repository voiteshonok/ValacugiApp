package by.voiteshonok.valacugi.ui.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import by.voiteshonok.valacugi.core.session.SessionRepository
import by.voiteshonok.valacugi.domain.MessageThread
import by.voiteshonok.valacugi.domain.ThreadsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DirectoryViewModel(
    private val threadsRepository: ThreadsRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<DirectoryUiState> = MutableStateFlow(DirectoryUiState())
    val uiState: StateFlow<DirectoryUiState> = _uiState.asStateFlow()

    init {
        observeThreads()
    }

    private fun observeThreads() {
        viewModelScope.launch {
            sessionRepository.observeSession()
                .map { session -> session?.identification }
                .flatMapLatest { userId: String? ->
                    if (userId.isNullOrBlank()) {
                        flowOf(emptyList())
                    } else {
                        threadsRepository.observeThreadsForUser(userId = userId)
                    }
                }
                .collect { threads: List<MessageThread> ->
                    _uiState.update { previousState ->
                        previousState.copy(threads = threads)
                    }
                }
        }
    }
}

class DirectoryViewModelFactory(
    private val threadsRepository: ThreadsRepository,
    private val sessionRepository: SessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DirectoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DirectoryViewModel(
                threadsRepository = threadsRepository,
                sessionRepository = sessionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
