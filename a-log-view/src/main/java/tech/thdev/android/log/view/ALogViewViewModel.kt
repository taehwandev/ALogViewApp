package tech.thdev.android.log.view

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import tech.thdev.android.log.repository.api.ALogRepository
import tech.thdev.android.log.view.compose.ui.theme.ALogColors
import tech.thdev.android.log.view.coroutines.ADispatcherProvider
import tech.thdev.android.log.view.model.ADragItem
import tech.thdev.android.log.view.model.ALogItem

class ALogViewViewModel(
    private val aLogRepository: ALogRepository,
    private val dispatcherProvider: ADispatcherProvider,
) {

    val logList = MutableStateFlow<List<ALogItem>>(emptyList())

    val logViewVisible = mutableStateOf(true)

    val dragItem = mutableStateOf(ADragItem(50, 50))

    @VisibleForTesting
    val flowDrag = MutableSharedFlow<ADragItem>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    @VisibleForTesting
    val flowChangeLogViewVisible = MutableSharedFlow<Boolean>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val coroutineScope by lazy {
        CoroutineScope(dispatcherProvider.default() + SupervisorJob())
    }

    fun run() {
        aLogRepository.runLog()
        flowRun().launchIn(coroutineScope)
        flowChangeLogViewVisible().launchIn(coroutineScope)
        flowDrag().launchIn(coroutineScope)
    }

    fun flowRun(): Flow<List<ALogItem>> =
        aLogRepository.flowLog()
            .map {
                it.asFlow().cancellable()
                    .map { item ->
                        val color = when (item.priority) {
                            Log.DEBUG -> ALogColors.DEBUG
                            Log.ERROR -> ALogColors.ERROR
                            Log.WARN -> ALogColors.WARN
                            Log.INFO -> ALogColors.INFO
                            else -> ALogColors.DEFAULT
                        }
                        ALogItem(
                            message = item.message,
                            color = color,
                        )
                    }
                    .toList()
            }
            .onEach { list ->
                logList.value = list
            }
            .flowOn(dispatcherProvider.main())

    fun flowChangeLogViewVisible(): Flow<Boolean> =
        flowChangeLogViewVisible
            .filter { it }
            .map {
                Log.i("TEMP", "change ${logViewVisible.value}")
                logViewVisible.value.not()
            }
            .onEach {
                Log.i("TEMP", "change value ${logViewVisible.value}")
                logViewVisible.value = it
            }
            .flowOn(dispatcherProvider.main())

    fun flowDrag(): Flow<ADragItem> =
        flowDrag
            .onEach {
                dragItem.value = it
            }
            .flowOn(dispatcherProvider.main())

    fun changeLogViewVisible() {
        Log.i("TEMP", "onClickViewStateChang ${logViewVisible.value}")
        flowChangeLogViewVisible.tryEmit(true)
    }

    fun drag(x: Int, y: Int) {
        flowDrag.tryEmit(ADragItem(x = x, y = y))
    }

    fun close() {
        aLogRepository.stopLog()
        coroutineScope.cancel()
    }
}