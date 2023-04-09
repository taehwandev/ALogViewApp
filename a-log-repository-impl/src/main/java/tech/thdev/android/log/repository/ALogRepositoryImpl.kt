package tech.thdev.android.log.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import tech.thdev.android.log.repository.api.ALogRepository
import tech.thdev.android.log.repository.api.model.ALogEntity

class ALogRepositoryImpl : ALogRepository {

    internal var sendLog = false

    private var cacheList = mutableListOf<ALogEntity>()

    override fun runLog() {
        sendLog = true
    }

    override fun stopLog() {
        sendLog = false
    }

    private val logSharedFlow = MutableStateFlow<List<ALogEntity>>(emptyList())

    override suspend fun getLastLog(takeLast: Int): List<ALogEntity> =
        synchronized(cacheList) {
            cacheList
                .reversed()
                .filter {
                    it.message.isNotEmpty()
                }
                .take(takeLast)
                .reversed()
        }

    override fun flowLog(): Flow<List<ALogEntity>> =
        logSharedFlow
            .filter { sendLog }
            .onEach {
                if (it.size == MAX_LOG_COUNT) {
                    cacheList.clear()
                }
            }

    override fun updateLog(priority: Int, tag: String?, message: String, t: Throwable?) {
        synchronized(cacheList) {
            cacheList.add(
                ALogEntity(
                    priority = priority,
                    tag = tag,
                    message = message,
                    t = t,
                )
            )

            logSharedFlow.value = cacheList.toList()
        }
    }

    companion object {

        const val MAX_LOG_COUNT = 500
    }
}