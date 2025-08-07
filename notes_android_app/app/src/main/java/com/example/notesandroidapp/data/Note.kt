package com.example.notesandroidapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// PUBLIC_INTERFACE
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val created: Long,
    val updated: Long
)
