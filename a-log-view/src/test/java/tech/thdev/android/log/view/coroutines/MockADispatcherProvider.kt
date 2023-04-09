package tech.thdev.android.log.view.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class MockADispatcherProvider : ADispatcherProvider {

    internal var testCoroutineDispatcher = UnconfinedTestDispatcher()

    override fun default(): CoroutineDispatcher =
        testCoroutineDispatcher

    override fun io(): CoroutineDispatcher =
        testCoroutineDispatcher

    override fun main(): CoroutineDispatcher =
        testCoroutineDispatcher

    override fun test(): CoroutineDispatcher =
        testCoroutineDispatcher
}