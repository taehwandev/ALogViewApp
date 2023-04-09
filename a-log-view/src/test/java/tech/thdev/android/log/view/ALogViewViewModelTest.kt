package tech.thdev.android.log.view

import android.util.Log
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import tech.thdev.android.log.repository.api.ALogRepository
import tech.thdev.android.log.repository.api.model.ALogEntity
import tech.thdev.android.log.view.compose.ui.theme.ALogColors
import tech.thdev.android.log.view.coroutines.MockADispatcherProvider
import tech.thdev.android.log.view.model.ADragItem
import tech.thdev.android.log.view.model.ALogItem

internal class ALogViewViewModelTest {

    private val aLogRepository = mock<ALogRepository>()

    private val viewModel = ALogViewViewModel(
        aLogRepository = aLogRepository,
        dispatcherProvider = MockADispatcherProvider(),
    )

    @Test
    fun `test initData`() {
        Assertions.assertTrue(viewModel.logList.value.isEmpty())
        Assertions.assertEquals(ADragItem(50, 50), viewModel.dragItem.value)
        Assertions.assertTrue(viewModel.logViewVisible.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test flowRun`() = runTest {
        val mockItem = listOf(
            ALogEntity(
                priority = Log.ERROR,
                message = "Error message",
                tag = null,
                t = null,
            )
        )
        whenever(aLogRepository.flowLog()).thenReturn(flowOf(mockItem))
        viewModel.flowRun()
            .test {
                val convertItem = mockItem.map { item ->
                    ALogItem(
                        message = item.message,
                        color = ALogColors.ERROR,
                    )
                }
                Assertions.assertEquals(convertItem, awaitItem())
                Assertions.assertEquals(convertItem, viewModel.logList.value)
                verify(aLogRepository).flowLog()

                cancelAndConsumeRemainingEvents()
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test flowChangeLogViewVisible`() = runTest {
        viewModel.flowChangeLogViewVisible()
            .test {
                Assertions.assertFalse(awaitItem())
                Assertions.assertFalse(viewModel.logViewVisible.value)

                viewModel.changeLogViewVisible()
                Assertions.assertTrue(viewModel.logViewVisible.value)
                Assertions.assertTrue(awaitItem())

                cancelAndConsumeRemainingEvents()
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test flowDrag`() = runTest {
        viewModel.flowDrag()
            .test {
                expectNoEvents()

                val change = ADragItem(100, 100)
                viewModel.drag(100, 100)
                Assertions.assertEquals(change, awaitItem())
                Assertions.assertEquals(change, viewModel.dragItem.value)

                cancelAndConsumeRemainingEvents()
            }
    }

    @Test
    fun `test close`() {
        viewModel.close()
        verify(aLogRepository).stopLog()
    }
}