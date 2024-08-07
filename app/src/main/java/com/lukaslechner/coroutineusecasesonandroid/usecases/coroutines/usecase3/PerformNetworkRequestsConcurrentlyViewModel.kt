package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val versionsFeaturesList =
                    listOf(getFeatures(27), getFeatures(28), getFeatures(29))
                uiState.value = UiState.Success(versionsFeaturesList)
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network request failed.")
            }
        }
    }

    fun performNetworkRequestsConcurrently() = runBlocking {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val versionFeaturesList = awaitAll(
                    async { getFeatures(27) },
                    async { getFeatures(28) },
                    async { getFeatures(29) }
                )
                uiState.value = UiState.Success(versionFeaturesList)
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network request failed.")
            }
        }
    }

    private suspend fun getFeatures(apiLevel: Int): VersionFeatures {
        return mockApi.getAndroidVersionFeatures(apiLevel)
    }
}
