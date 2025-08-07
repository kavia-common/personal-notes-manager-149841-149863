package com.example.notesandroidapp.ui

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notesandroidapp.R
import com.example.notesandroidapp.data.Note

// PUBLIC_INTERFACE
class NoteAdapter(
    private var notes: List<Note>,
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val noteTitle: TextView = view.findViewById(R.id.noteTitle)
        private val noteContent: TextView = view.findViewById(R.id.noteContent)
        private val noteDate: TextView = view.findViewById(R.id.noteDate)

        fun bind(note: Note) {
            noteTitle.text = note.title
            noteContent.text = note.content.lines().firstOrNull()?.take(64) ?: ""
            noteDate.text = android.text.format.DateFormat.format("MMM dd, yyyy", note.updated)
            itemView.setOnClickListener { onClick(note) }
            itemView.setOnLongClickListener {
                onLongClick(note)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = holder.bind(notes[position])
    override fun getItemCount(): Int = notes.size
}
