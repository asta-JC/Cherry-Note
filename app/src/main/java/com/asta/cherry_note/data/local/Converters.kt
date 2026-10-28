package com.asta.cherry_note.data.local

import androidx.room.TypeConverter
import com.asta.cherry_note.data.model.ChecklistItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return try {
            json.decodeFromString(value)
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromChecklistItems(value: List<ChecklistItem>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toChecklistItems(value: String): List<ChecklistItem> {
        if (value.isBlank()) return emptyList()
        return try {
            json.decodeFromString(value)
        } catch (_: Exception) {
            emptyList()
        }
    }
}
