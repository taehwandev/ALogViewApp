package tech.thdev.android.log.view

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import tech.thdev.android.log.repository.ALogRepositoryImpl
import tech.thdev.android.log.repository.timber.ALogTimberDebug
import tech.thdev.android.log.view.compose.ALogMessageScreen
import tech.thdev.android.log.view.compose.ALogScreen
import tech.thdev.android.log.view.coroutines.ADispatcherProvider
import tech.thdev.android.log.view.model.ADragItem
import timber.log.Timber

class ALogViewService : Service() {

    private val aLogRepository by lazy {
        ALogRepositoryImpl()
    }

    private val aLogViewViewModel by lazy {
        ALogViewViewModel(
            aLogRepository = aLogRepository,
            dispatcherProvider = ADispatcherProvider.invoke(),
        )
    }

    private val windowManager by lazy {
        ContextCompat.getSystemService(this, WindowManager::class.java)
    }

    private val params by lazy {
        WindowManager.LayoutParams().apply {
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSLUCENT
        }
    }

    private var composeView: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(ALogTimberDebug(aLogRepository)) // Timber append.
        if (Settings.canDrawOverlays(this)) {
            createComposeView()
            aLogViewViewModel.run()
        } else {
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        composeView?.let { // Remove view
            windowManager?.removeViewImmediate(it)
        }
    }

    private fun createComposeView() {
        composeView = ComposeView(this).apply {
            setContent {
                CompositionLocalProvider {
                    ContentView(params, windowManager)
                }
            }
        }

        val composeLifecycleServiceOwner = AComposeLifecycleServiceOwner().apply {
            performRestore(null)
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
        composeView!!.setViewTreeLifecycleOwner(composeLifecycleServiceOwner)
        composeView!!.setViewTreeSavedStateRegistryOwner(composeLifecycleServiceOwner)
        windowManager?.addView(composeView, params)
    }

    @Composable
    private fun ComposeView.ContentView(params: WindowManager.LayoutParams, windowManager: WindowManager?) {
        val logList by aLogViewViewModel.logList.collectAsState()
        val logViewVisible by aLogViewViewModel.logViewVisible.asRemember()
        ALogScreen(
            onClickViewStateChang = {
                aLogViewViewModel.changeLogViewVisible()
            },
            onClickClose = {
                aLogViewViewModel.close()
                stopSelf()
            },
            dragEvent = { x, y ->
                aLogViewViewModel.drag(x, y)
            },
            logViewVisible = logViewVisible,
            logMessageView = {
                ALogMessageScreen(items = logList)
            }
        )

        val dragItem by aLogViewViewModel.dragItem.asRemember()
        onDrag(dragItem, params, windowManager)
    }

    private fun ComposeView.onDrag(
        dragItem: ADragItem,
        params: WindowManager.LayoutParams,
        windowManager: WindowManager?,
    ) {
        params.x += dragItem.x
        params.y += dragItem.y
        windowManager?.updateViewLayout(this, params)
    }

    @Composable
    private fun <T> MutableState<T>.asRemember(): State<T> {
        return remember { this }
    }

    companion object {

        fun newInstance(context: Context): Intent =
            Intent(context, ALogViewService::class.java)
    }
}