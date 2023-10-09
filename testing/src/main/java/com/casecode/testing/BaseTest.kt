package com.casecode.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.GetStoreUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.repository.TestSubscriptionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.Extensions

@ExperimentalCoroutinesApi
abstract class BaseTest
{
   

   
   // Set the main coroutines dispatcher for unit testing.
   @get:Extensions
   var mainCoroutineRule = CoroutinesTestExtension()
   
   @Rule
   @JvmField
   val instantTaskExecutorRule = InstantTaskExecutorRule()
   
   private lateinit var testBusinessRepository: TestBusinessRepository
   private lateinit var testPlansRepository: TestSubscriptionsRepository
   
   
   private lateinit var getBusinessUseCase: GetBusinessUseCase
   lateinit var setBusinessUseCase: SetBusinessUseCase
   lateinit var getStoreUseCase: GetStoreUseCase
   lateinit var getPlanUseCase: GetSubscriptionsUseCase
   
   @BeforeEach
   fun setup()
   {
      testBusinessRepository = TestBusinessRepository()
      testPlansRepository = TestSubscriptionsRepository()
      
      getBusinessUseCase = GetBusinessUseCase(testBusinessRepository)
      setBusinessUseCase = SetBusinessUseCase(testBusinessRepository)
      getPlanUseCase = GetSubscriptionsUseCase(testPlansRepository)
      
      init()
   }
   
   abstract fun init()
   
   
   companion object
   {
      const val ERROR_MESSAGE_LIVEDATA_NULL = "LiveData has null value"
   }
}