package com.example.schetodo.feature.dbbackup

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.example.schetodo.data.BACKUP_FILE_MIME_TYPE
import com.example.schetodo.data.DatabaseDao
import com.example.schetodo.data.SchetodoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

class DatabaseBackupExporter(
    private val context: Context,
    private val databaseDao: DatabaseDao,
) {
    private companion object {
        private const val BACKUP_FILE_PREFIX = "Schetodo_Backup"
    }

    suspend fun exportDatabaseToDirectory(destinationDirectoryUri: Uri) {
        val dbFilePath = context.getDatabasePath(SchetodoDatabase.DATABASE_NAME)

        val destinationDirectory = DocumentFile.fromTreeUri(context, destinationDirectoryUri)
        if (destinationDirectory == null || !destinationDirectory.exists()
            || !destinationDirectory.isDirectory
        ) {
            throw IllegalArgumentException("Destination directory does not exist.")
        }

        val backupFile = destinationDirectory.createFile(
            BACKUP_FILE_MIME_TYPE, createBackupFileName()
        ) ?: throw IOException("Failed to create backup file.")

        withContext(Dispatchers.IO) {
            databaseDao.checkpointDatabase()

            val backupFileOutputStream =
                context.contentResolver.openOutputStream(backupFile.uri)
                    ?: throw IOException("Failed to open output stream to backup file.")

            dbFilePath.inputStream().use { input ->
                backupFileOutputStream.use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun createBackupFileName(): String {
        val currentDate = LocalDate.now()
        return "${BACKUP_FILE_PREFIX}_${currentDate}_${System.currentTimeMillis()}"
    }
}
