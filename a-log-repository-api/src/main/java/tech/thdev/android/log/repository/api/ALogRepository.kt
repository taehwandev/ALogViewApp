package tech.thdev.android.log.repository.api

import kotlinx.coroutines.flow.Flow
import tech.thdev.android.log.repository.api.model.ALogEntity

interface ALogRepository {

    fun runLog()

    fun stopLog()

    fun flowLog(): Flow<List<ALogEntity>>

    suspend fun getLastLog(takeLast: Int): List<ALogEntity>

    fun updateLog(priority: Int, tag: String?, message: String, t: Throwable?)
}