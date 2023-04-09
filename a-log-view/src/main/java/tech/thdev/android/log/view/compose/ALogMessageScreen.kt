package tech.thdev.android.log.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tech.thdev.android.log.view.compose.ui.theme.ALogColors
import tech.thdev.android.log.view.model.ALogItem

@Composable
internal fun ALogMessageScreen(
    items: List<ALogItem>,
) {
    val autoScrollDistance by remember { mutableStateOf(30) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(items.size) {
        val count = scrollState.layoutInfo.totalItemsCount

        if (count > 0) {
            delay(200)
            scrollState.scrollToItem(items.lastIndex)
        }

        if (items.isNotEmpty()) {
            scrollState.canAutoScroll(autoScrollDistance) {
                scrollState.scrollToItem(it)
            }
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(2.dp)
            .background(color = ALogColors.WHITE)
    ) {
        itemsIndexed(items) { _, item ->
            Column {
                Text(
                    text = item.message,
                    color = item.color,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )

                Divider()
            }
        }
    }
}

@Preview
@Composable
internal fun PreviewALogMessageScreen() {
    ALogMessageScreen(
        items = listOf(
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

/**
 * find scroll position is end
 * @param action return last item position
 */
inline fun LazyListState.canAutoScroll(distance: Int, action: (Int) -> Unit) {
    val visibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return
    val totalCount = layoutInfo.totalItemsCount
    if (visibleItemIndex >= totalCount - distance && visibleItemIndex < totalCount) {
        action(totalCount - 1)
    }
}
