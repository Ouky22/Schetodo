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
    fun when_getting_all_templates_only_templates_not_marked_for_deletion_are_returned() = runTest {
        val template1 = ScheduleTemplate(1, "t1", markedForDeletion = true)
        val template2 = ScheduleTemplate(2, "t2")
        scheduleTemplateDao.insert(template1).toInt()
        scheduleTemplateDao.insert(template2).toInt()

        assertThat(scheduleTemplateDao.getAll().first()).containsExactly(template2)
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