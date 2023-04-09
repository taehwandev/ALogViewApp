package tech.thdev.android.log.repository.timber

import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import tech.thdev.android.log.repository.api.ALogRepository

internal class ALogTimberDebugTest {

    private val aLogRepository = mock<ALogRepository>()

    private val timber = ALogTimberDebug(aLogRepository)

    @Test
    fun `test updateLog`() {
        timber.log(0, "message")
        aLogRepository.updateLog(0, tag = null, message = "message", t = null)
    }
}