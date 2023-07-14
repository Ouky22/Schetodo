package com.example.schetodo.data.schedule_template

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.schetodo.data.SchetodoDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class ScheduleTemplateDaoTest {

    private lateinit var scheduleTemplateDao: ScheduleTemplateDao
    private lateinit var db: SchetodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SchetodoDatabase::class.java).build()
        scheduleTemplateDao = db.scheduleTemplateDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun test_marking_schedule_template_for_deletion() = runTest {
        val template = ScheduleTemplate(0, "t1")
        val templateId = scheduleTemplateDao.insert(template).toInt()

        scheduleTemplateDao.markForDeletion(templateId)

        assertThat(scheduleTemplateDao.getById(templateId).first()?.markedForDeletion).isTrue()
    }

    @Test
    fun test_unmarking_schedule_template_for_deletion() = runTest {
        val template = ScheduleTemplate(0, "t1")
        val templateId = scheduleTemplateDao.insert(template).toInt()

        scheduleTemplateDao.markForDeletion(templateId)
        scheduleTemplateDao.unmarkForDeletion(templateId)

        assertThat(scheduleTemplateDao.getById(templateId).first()?.markedForDeletion).isFalse()
    }
}