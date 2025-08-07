package com.example.notesandroidapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesandroidapp.R
import com.example.notesandroidapp.data.NotesDatabase
import com.example.notesandroidapp.repo.NotesRepository
import com.example.notesandroidapp.viewmodel.NotesViewModel
import com.example.notesandroidapp.viewmodel.NotesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var noteAdapter: NoteAdapter

    private val noteEditLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Handle result for add/update (force refresh via LiveData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        NotesAppTheme.setDarkMode(this, NotesAppTheme.isDarkMode(this))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = NotesDatabase.getInstance(this)
        val repo = NotesRepository(db.noteDao())
        viewModel = ViewModelProvider(this, NotesViewModelFactory(repo)).get(NotesViewModel::class.java)

        val notesRecycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.notesRecycler)
        notesRecycler.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(emptyList(),
            onClick = { note ->
                val intent = Intent(this, NoteEditActivity::class.java)
                intent.putExtra("noteId", note.id)
                noteEditLauncher.launch(intent)
            },
            onLongClick = { note ->
                android.app.AlertDialog.Builder(this)
                    .setMessage(R.string.delete_note_confirm)
                    .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteNote(note) }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            })
        notesRecycler.adapter = noteAdapter

        val fab = findViewById<FloatingActionButton>(R.id.fabAddNote)
        fab.setOnClickListener {
            val intent = Intent(this, NoteEditActivity::class.java)
            noteEditLauncher.launch(intent)
        }

        lifecycleScope.launch {
            viewModel.notes.collectLatest {
                noteAdapter.updateNotes(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.queryHint = getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_theme -> {
                val current = NotesAppTheme.isDarkMode(this)
                NotesAppTheme.setDarkMode(this, !current)
                recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
