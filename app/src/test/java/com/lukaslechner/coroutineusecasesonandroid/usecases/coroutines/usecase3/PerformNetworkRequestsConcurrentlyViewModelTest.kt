package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import com.lukaslechner.coroutineusecasesonandroid.utils.ReplaceMainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class PerformNetworkRequestsConcurrentlyViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get: Rule
    val replaceMainDispatcherRule = ReplaceMainDispatcherRule()

    private val receivedUiStates = mutableListOf<UiState>()

    @Test
    fun `uc3 return data after 3 times the response delay`() = runTest {
            val responseDelay = 1000L
            val fakeApi = FakeSuccessApi(responseDelay)
            val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)

            viewModel.observe()

            Assert.assertTrue(receivedUiStates.isEmpty())

            viewModel.performNetworkRequestsSequentially()

            advanceUntilIdle()

            Assert.assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Success(
                        listOf(
                            mockVersionFeaturesOreo,
                            mockVersionFeaturesPie,
                            mockVersionFeaturesAndroid10
                        )
                    )
                ), receivedUiStates
            )

            Assert.assertEquals(3000, currentTime)
        }

    @Test
    fun `uc3 return data after the response delay`() = runTest {
            val responseDelay = 1000L
            val fakeApi = FakeSuccessApi(responseDelay)
            val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)

            viewModel.observe()

            Assert.assertTrue(receivedUiStates.isEmpty())

            viewModel.performNetworkRequestsConcurrently()

            advanceUntilIdle()

            Assert.assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Success(
                        listOf(
                            mockVersionFeaturesOreo,
                            mockVersionFeaturesPie,
                            mockVersionFeaturesAndroid10
                        )
                    )
                ), receivedUiStates
            )

            Assert.assertEquals(1000, currentTime)
        }

    @Test
    fun `uc3 return Error when network request fails`() = runTest {
            val responseDelay = 1000L
            val fakeApi = FakeErrorApi(responseDelay)
            val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)

            viewModel.observe()

            Assert.assertTrue(receivedUiStates.isEmpty())

            viewModel.performNetworkRequestsConcurrently()

            advanceUntilIdle()

            Assert.assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Error("Network request failed.")
                ), receivedUiStates
            )
        }

    private fun PerformNetworkRequestsConcurrentlyViewModel.observe() {
        uiState().observeForever { uiState ->
            if (uiState != null) {
                receivedUiStates.add(uiState)
            }
        }
    }
}
