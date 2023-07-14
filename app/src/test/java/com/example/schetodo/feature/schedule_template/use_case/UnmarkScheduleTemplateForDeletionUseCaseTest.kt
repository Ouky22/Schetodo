package com.example.schetodo.feature.schedule_template.use_case

import com.example.schetodo.data.FakeScheduleTemplateRepository
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import com.example.schetodo.data.todo_block.FakeTodoBlockRepository
import com.example.schetodo.data.todo_block.TodoBlock
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalCoroutinesApi
class UnmarkScheduleTemplateForDeletionUseCaseTest {

    private val fakeTemplateRepository = FakeScheduleTemplateRepository()
    private val fakeTodoBlockRepository = FakeTodoBlockRepository()
    private val unmarkScheduleTemplateForDeletionUseCase = UnmarkScheduleTemplateForDeletionUseCase(
        fakeTemplateRepository,
        fakeTodoBlockRepository
    )

    @Test
    fun when_unmarking_schedule_template_for_deletion_then_blocks_are_unmarked_for_deletion() = runTest {
        val template1 = ScheduleTemplate(1, "t1")
        val template2 = ScheduleTemplate(2, "t2", true)
        val date = LocalDate.now()
        val time = LocalTime.now()
        val todoBlock1 = TodoBlock(1, "t1", date, time, time, template1.templateId)
        val todoBlock2 = TodoBlock(2, "t2", date, time, time, template1.templateId)
        val todoBlock3 = TodoBlock(3, "t3", date, time, time, template2.templateId, true)
        fakeTemplateRepository.insert(template1)
        fakeTemplateRepository.insert(template2)
        fakeTodoBlockRepository.insertTodoBlock(todoBlock1)
        fakeTodoBlockRepository.insertTodoBlock(todoBlock2)
        fakeTodoBlockRepository.insertTodoBlock(todoBlock3)

        unmarkScheduleTemplateForDeletionUseCase(templateId = template1.templateId)

        assertThat(
            fakeTemplateRepository.getById(template1.templateId).first()?.markedForDeletion
        ).isFalse()

        assertThat(
            fakeTemplateRepository.getById(template2.templateId).first()?.markedForDeletion
        ).isTrue()

        assertThat(
            fakeTodoBlockRepository.getBlockById(todoBlock1.todoBlockId).first()?.markedForDeletion
        ).isFalse()

        assertThat(
            fakeTodoBlockRepository.getBlockById(todoBlock2.todoBlockId).first()?.markedForDeletion
        ).isFalse()

        assertThat(
            fakeTodoBlockRepository.getBlockById(todoBlock3.todoBlockId).first()?.markedForDeletion
        ).isTrue()
    }
}