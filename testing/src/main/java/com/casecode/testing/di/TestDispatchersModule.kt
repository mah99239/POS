package com.casecode.testing.di

import com.casecode.data.utils.AppDispatchers
import com.casecode.data.utils.Dispatcher
import com.casecode.di.data.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher


@Module
@TestInstallIn(
   components = [SingletonComponent::class],
   replaces = [DispatchersModule::class],
              )
object TestDispatchersModule {
   @Provides
   @Dispatcher(AppDispatchers.IO)
   fun providesIODispatcherTest(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher
    @Provides
   @Dispatcher(AppDispatchers.MAIN)
   fun providesMainDispatcherTest(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

   @Provides
   @Dispatcher(AppDispatchers.DEFAULT)
   fun providesDefaultDispatcherTest(
        testDispatcher: TestDispatcher,
                                ): CoroutineDispatcher = testDispatcher
}