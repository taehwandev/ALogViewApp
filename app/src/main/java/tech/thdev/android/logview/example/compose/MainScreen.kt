package tech.thdev.android.logview.example.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.thdev.android.logview.example.ui.theme.MainTheme
import timber.log.Timber

@Composable
internal fun MainScreen(
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                text = "LogView start!!",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        var count by remember { mutableStateOf(0) }
        Button(
            onClick = {
                when (count % 4) {
                    1 -> Timber.i("Timber info log $count")
                    2 -> Timber.e("Timber error log $count")
                    3 -> Timber.w("Timber warn log $count")
                    else -> Timber.d("Timber debug log $count")
                }
                count += 1
            },
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                text = "Log test",
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun PreviewMainScreen() {
    MainTheme {
        MainScreen()
    }
}