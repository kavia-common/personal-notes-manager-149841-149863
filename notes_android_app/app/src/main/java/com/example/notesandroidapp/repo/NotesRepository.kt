package com.example.notesandroidapp.repo

import com.example.notesandroidapp.data.Note
import com.example.notesandroidapp.data.NoteDao
import kotlinx.coroutines.flow.Flow

// PUBLIC_INTERFACE
class NotesRepository(private val dao: NoteDao) {
    fun getAll(): Flow<List<Note>> = dao.getAllNotes()
    suspend fun getById(id: Long): Note? = dao.getNoteById(id)
    suspend fun insert(note: Note): Long = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)
    fun search(query: String): Flow<List<Note>> = dao.searchNotes(query)
}
