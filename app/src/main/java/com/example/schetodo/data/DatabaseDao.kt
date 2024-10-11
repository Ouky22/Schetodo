package com.example.schetodo.data

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface DatabaseDao {
    @RawQuery
    suspend fun checkpointDatabase(query: SupportSQLiteQuery): Int

    suspend fun checkpointDatabase() {
        // SQLite uses a write-ahead log (WAL) to improve performance.
        // For backup purposes, the WAL file needs to be merged with the main SQLite database file
        // so that all data can be exported as a single file (otherwise, the WAL file would also
        // have to be exported, which makes the file handling and the later import more difficult).
        // The merge is done by calling the PRAGMA wal_checkpoint(full) command.
        checkpointDatabase(SimpleSQLiteQuery("PRAGMA wal_checkpoint(FULL)"))
    }
}
