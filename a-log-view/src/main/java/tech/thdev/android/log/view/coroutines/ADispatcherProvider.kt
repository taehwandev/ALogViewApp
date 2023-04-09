package tech.thdev.android.log.view.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface ADispatcherProvider {

    fun default(): CoroutineDispatcher

    fun io(): CoroutineDispatcher

    fun main(): CoroutineDispatcher

    fun test(): CoroutineDispatcher

    companion object {
        operator fun invoke(): ADispatcherProvider = object : ADispatcherProvider {

            override fun default(): CoroutineDispatcher =
                Dispatchers.Default

            override fun io(): CoroutineDispatcher =
                Dispatchers.IO

            override fun main(): CoroutineDispatcher =
                Dispatchers.Main.immediate

            override fun test(): CoroutineDispatcher =
                Dispatchers.Unconfined
        }
    }
}