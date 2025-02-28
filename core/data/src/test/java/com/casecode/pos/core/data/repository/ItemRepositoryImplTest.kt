package com.casecode.pos.core.data.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ItemRepositoryImplTest {
    // Subject under test.
    private val testItemRepository: TestItemRepository = TestItemRepository()

    @Before
    fun setup() {
    }

    @Test
    fun getItems_whenHasItems_returnsResourceSuccessItems() =
        runTest {
            // When - get items
            val actualResult = testItemRepository.getItems().first()
            // Then - assert that the result is success
            assertEquals(Resource.Success(testItemRepository.fakeListItems), actualResult)
        }

    @Test
    fun getItems_whenNoItems_returnsResourceEmpty() =
        runTest {
            // Given - set return empty items
            testItemRepository.setReturnEmpty(true)
            // When - get items
            val actualResult = testItemRepository.getItems().first()
            // Then - assert that the result is success
            assertEquals(Resource.empty(), actualResult)
        }

    @Test
    fun getItems_whenError_returnsResourceError() =
        runTest {
            // Given - set return error
            testItemRepository.setReturnError(true)
            // When - get items
            val actualResult = testItemRepository.getItems().first()
            // Then - assert that the result is success
            assertEquals(Resource.error("Error"), actualResult)
        }
}