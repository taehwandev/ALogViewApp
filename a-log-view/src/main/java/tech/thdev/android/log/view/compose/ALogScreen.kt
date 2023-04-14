package tech.thdev.android.log.view.compose

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import kotlinx.collections.immutable.persistentListOf
import tech.thdev.android.log.view.R
import tech.thdev.android.log.view.compose.ui.theme.ALogColors
import tech.thdev.android.log.view.model.ALogItem

var prevX = 0.0f
var prevY = 0.0f
var isDrag = false

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun ALogScreen(
    onClickViewStateChang: () -> Unit,
    onClickClose: () -> Unit,
    dragEvent: (x: Int, y: Int) -> Unit,
    logViewVisible: Boolean,
    logMessageView: @Composable () -> Unit,
) {
    ConstraintLayout {
        val (icon, container) = createRefs()

        Surface(
            color = ALogColors.BLACK.copy(alpha = 0.5f),
            modifier = Modifier
                .width(230.dp)
                .constrainAs(container) {
                    linkTo(start = parent.start, end = parent.end)
                    linkTo(top = parent.top, bottom = parent.bottom)
                    visibility = Visibility.Visible.takeIf { logViewVisible } ?: Visibility.Gone
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(3.dp)
                    .background(ALogColors.WHITE.copy(alpha = 0.7f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = ALogColors.WHITE)
                ) {
                    Text(
                        text = "Log view",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp, end = 10.dp)
                    )

                    IconButton(
                        onClick = { onClickClose() },
                        modifier = Modifier
                            .size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cancel_24),
                            contentDescription = null,
                        )
                    }
                }
                logMessageView()
            }
        }

        Box(
            modifier = Modifier
                .pointerInteropFilter {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            isDrag = false

                            prevX = it.rawX
                            prevY = it.rawY

                            true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val rawX = it.rawX
                            val rawY = it.rawY

                            val newX = rawX - prevX
                            val newY = rawY - prevY

                            dragEvent(newX.toInt(), newY.toInt())

                            if (isDrag.not()) {
                                isDrag = (newX > 2f || newX < -2f) && (newY > 2f || newY < -2f)
                            }

                            prevX = rawX
                            prevY = rawY
                            true
                        }

                        MotionEvent.ACTION_UP -> {
                            false
                        }

                        else -> false
                    }
                }
                .constrainAs(icon) {
                    linkTo(start = container.start, end = container.start)
                    linkTo(top = container.top, bottom = container.top)
                }
        ) {
            IconButton(
                onClick = { onClickViewStateChang() },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_adb_24),
                    tint = ALogColors.DEBUG,
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                )
            }
        }
    }
}

@Preview
@Composable
internal fun PreviewALogScreen() {
    ALogScreen(
        logViewVisible = true,
        onClickClose = {},
        onClickViewStateChang = {},
        dragEvent = { _, _ -> },
        logMessageView = {
            ALogMessageScreen(
                items = persistentListOf(
                    ALogItem(
                        message = "Log",
                        color = ALogColors.INFO,
                    ),
                    ALogItem(
                        message = "Error Log",
                        color = ALogColors.ERROR,
                    ),
                )
            )
        }
    )
}