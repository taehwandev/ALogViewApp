package tech.thdev.android.log.view.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ALogItem(
    val message: String,
    val color: Color,
)