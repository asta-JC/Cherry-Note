package com.asta.cherry_note.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.asta.cherry_note.data.model.CollectionEntity
import com.asta.cherry_note.data.model.NoteEntity

@Database(
    entities = [NoteEntity::class, CollectionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CherryDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        @Volatile
        private var INSTANCE: CherryDatabase? = null

        fun getInstance(context: Context): CherryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CherryDatabase::class.java,
                    "cherry_note.db"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
