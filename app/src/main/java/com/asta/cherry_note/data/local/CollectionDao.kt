package com.asta.cherry_note.data.local

import androidx.room.Dao
import androidx.room.Query
import com.asta.cherry_note.data.model.CollectionEntity
import kotlinx.coroutines.flow.Flow

/** Read-only access to recover collections from the experimental database. */
@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY name")
    fun observeAllCollections(): Flow<List<CollectionEntity>>
}
