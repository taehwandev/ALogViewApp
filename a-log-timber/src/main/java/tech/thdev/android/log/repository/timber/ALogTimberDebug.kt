package tech.thdev.android.log.repository.timber

import tech.thdev.android.log.repository.api.ALogRepository
import timber.log.Timber

class ALogTimberDebug(
    private val aLogRepository: ALogRepository,
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        aLogRepository.updateLog(priority, tag, message, t)
    }
}