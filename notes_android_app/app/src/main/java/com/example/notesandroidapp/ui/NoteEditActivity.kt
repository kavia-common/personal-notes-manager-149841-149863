package com.example.notesandroidapp.ui

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.notesandroidapp.R
import com.example.notesandroidapp.data.Note
import com.example.notesandroidapp.data.NotesDatabase
import com.example.notesandroidapp.repo.NotesRepository
import com.example.notesandroidapp.viewmodel.NotesViewModel
import com.example.notesandroidapp.viewmodel.NotesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteEditActivity : AppCompatActivity() {
    private lateinit var viewModel: NotesViewModel
    private var noteId: Long = 0L
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        NotesAppTheme.setDarkMode(this, NotesAppTheme.isDarkMode(this))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_edit)

        val db = NotesDatabase.getInstance(this)
        val repo = NotesRepository(db.noteDao())
        viewModel = ViewModelProvider(this, NotesViewModelFactory(repo)).get(NotesViewModel::class.java)

        val titleInput = findViewById<EditText>(R.id.editTitle)
        val contentInput = findViewById<EditText>(R.id.editContent)
        val fabSave = findViewById<FloatingActionButton>(R.id.fabSaveNote)

        noteId = intent.getLongExtra("noteId", 0L)
        isEditing = noteId != 0L

        if (isEditing) {
            CoroutineScope(Dispatchers.Main).launch {
                val note = viewModel.getNoteById(noteId)
                if (note != null) {
                    titleInput.setText(note.title)
                    contentInput.setText(note.content)
                }
            }
        }

        fabSave.setOnClickListener {
            val now = System.currentTimeMillis()
            val title = titleInput.text.toString().trim()
            val content = contentInput.text.toString().trim()
            if (title.isEmpty() && content.isEmpty()) {
                finish()
                return@setOnClickListener
            }
            val note = if (isEditing) {
                Note(id = noteId, title = title, content = content, created = now, updated = now)
            } else {
                Note(title = title, content = content, created = now, updated = now)
            }
            viewModel.addOrUpdateNote(note)
            finish()
        }
    }
}
