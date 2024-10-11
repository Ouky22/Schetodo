package com.example.schetodo.feature.dbbackup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.documentfile.provider.DocumentFile
import com.example.schetodo.data.SchetodoDatabase
import com.example.schetodo.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class DatabaseBackupImporter(
    private val context: Context,
) {

    suspend fun importDatabase(databaseBackupFileUri: Uri) {
        validateBackupFile(databaseBackupFileUri)

        withContext(Dispatchers.IO) {
            val databaseName = SchetodoDatabase.DATABASE_NAME

            val currentDbFile = context.getDatabasePath(databaseName)
            currentDbFile.parent.let { dbPath ->
                val shmFile = File(dbPath, "$databaseName-shm")
                val walFile = File(dbPath, "$databaseName-wal")
                shmFile.delete()
                walFile.delete()
            }

            val backupFileInputStream =
                context.contentResolver.openInputStream(databaseBackupFileUri)
                    ?: throw IllegalArgumentException("Failed to open input stream to backup file.")

            currentDbFile.outputStream().use { output ->
                backupFileInputStream.use { input ->
                    input.copyTo(output)
                }
            }

            // needed so that database and all flows from the database are reset to a fresh state
            restartApp()
        }
    }

    private fun validateBackupFile(databaseBackupFileUri: Uri) {
        val databaseBackupFile = DocumentFile.fromSingleUri(context, databaseBackupFileUri)

        if (databaseBackupFile == null || !databaseBackupFile.exists()) {
            throw IllegalArgumentException("Backup file does not exist.")
        }

        if (databaseBackupFile.isDirectory) {
            throw IllegalArgumentException("Backup file is not a file.")
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun restartApp() {
        val restartAppIntent = Intent(context, MainActivity::class.java)
        restartAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(restartAppIntent)
        if (context is Activity) {
            context.finish()
        }
        Runtime.getRuntime().exit(0)
    }
}
