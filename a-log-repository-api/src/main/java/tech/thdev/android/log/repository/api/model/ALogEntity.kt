package tech.thdev.android.log.repository.api.model

data class ALogEntity(
    val priority: Int,
    val tag: String?,
    val message: String,
    val t: Throwable?,
)
