package com.asta.cherry_note.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isDone: Boolean = false
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val isPinned: Boolean = false,
    val colorHex: String = "#FFFFFF",
    val tags: List<String> = emptyList(),
    val collectionId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val checklistItems: List<ChecklistItem> = emptyList(),
    val isDeleted: Boolean = false
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "folder",
    val colorHex: String = "#E91E63",
    val createdAt: Long = System.currentTimeMillis()
)
