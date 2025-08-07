package com.example.notesandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notesandroidapp.data.Note
import com.example.notesandroidapp.repo.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// PUBLIC_INTERFACE
class NotesViewModel(private val repo: NotesRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _allNotes: Flow<List<Note>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repo.getAll() else repo.search(query)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val notes: StateFlow<List<Note>> = _allNotes.stateIn(
        viewModelScope, SharingStarted.Eagerly, emptyList()
    )

    private val _themeDark = MutableStateFlow(false)
    val themeDark: StateFlow<Boolean> = _themeDark

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTheme() {
        _themeDark.value = !_themeDark.value
    }

    fun setTheme(isDark: Boolean) {
        _themeDark.value = isDark
    }

    fun addOrUpdateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                repo.insert(note)
            } else {
                repo.update(note)
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(note)
        }
    }

    suspend fun getNoteById(id: Long): Note? = repo.getById(id)
}

// PUBLIC_INTERFACE
class NotesViewModelFactory(private val repo: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
