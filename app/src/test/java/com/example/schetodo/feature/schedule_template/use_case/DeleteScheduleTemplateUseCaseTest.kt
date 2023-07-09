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
class DeleteScheduleTemplateUseCaseTest {

    private val fakeTemplateRepository = FakeScheduleTemplateRepository()
    private val fakeTodoBlockRepository = FakeTodoBlockRepository()
    private val deleteScheduleTemplateUseCase = DeleteScheduleTemplateUseCase(
        fakeTemplateRepository,
        fakeTodoBlockRepository
    )

    @Test
    fun when_deleting_schedule_template_then_corresponding_todo_blocks_are_deleted() = runTest {
        val template = ScheduleTemplate(1, "t1")
        val date = LocalDate.now()
        val time = LocalTime.now()
        val todoBlock1 = TodoBlock(1, "t1", date, time, time, template.templateId)
        val todoBlock2 = TodoBlock(2, "t2", date, time, time, template.templateId)
        fakeTemplateRepository.insert(template)
        fakeTodoBlockRepository.insertTodoBlock(todoBlock1)
        fakeTodoBlockRepository.insertTodoBlock(todoBlock2)

        deleteScheduleTemplateUseCase(templateId = template.templateId)

        assertThat(fakeTemplateRepository.getById(template.templateId).first()).isNull()
        assertThat(fakeTodoBlockRepository.getBlockById(todoBlock1.todoBlockId).first()).isNull()
        assertThat(fakeTodoBlockRepository.getBlockById(todoBlock2.todoBlockId).first()).isNull()
    }
}