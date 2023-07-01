package com.example.schetodo.data.todo

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.schetodo.data.SchetodoDatabase
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DB_NAME = "test"

@RunWith(AndroidJUnit4::class)
class TodoMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SchetodoDatabase::class.java
    )

    @Test
    fun when_migrating_from_version_3_to_4_then_markedForDeletion_is_added_with_default_value() {
        helper.createDatabase(DB_NAME, 3).apply {
            execSQL("INSERT INTO Todo VALUES(0, 'test', 1, 1, 1)")
            close()
        }

        val db = helper.runMigrationsAndValidate(DB_NAME, 4, true)
        db.query("SELECT * FROM Todo").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getInt(getColumnIndex("markedForDeletion"))).isEqualTo(0)
        }
    }
}