package tech.thdev.android.log.repository

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import tech.thdev.android.log.repository.api.model.ALogEntity

internal class ALogRepositoryImplTest {

    private val repository = ALogRepositoryImpl()

    @Test
    fun `test changeUseLog`() {
        Assertions.assertFalse(repository.sendLog)
        repository.runLog()
        Assertions.assertTrue(repository.sendLog)
        repository.stopLog()
        Assertions.assertFalse(repository.sendLog)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test flowLog`() = runTest {
        repository.flowLog()
            .test {
                expectNoEvents()

                // Not use log
                repository.updateLog(0, "", "New message", null)
                expectNoEvents()

                repository.runLog()
                repository.updateLog(0, "", "message 2", null)
                val newList = listOf(
                    ALogEntity(0, "", "New message", null),
                    ALogEntity(0, "", "message 2", null),
                )
                Assertions.assertEquals(newList, awaitItem())

                // Last copy test
                Assertions.assertEquals(newList, repository.getLastLog(30))

                cancelAndConsumeRemainingEvents()
            }
    }
}