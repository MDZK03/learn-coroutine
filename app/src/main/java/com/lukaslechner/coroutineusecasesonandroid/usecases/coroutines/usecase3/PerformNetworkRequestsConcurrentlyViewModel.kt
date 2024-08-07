package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
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
                val oreoFeatures = mockApi.getAndroidVersionFeatures(27)
                val pieFeatures = mockApi.getAndroidVersionFeatures(28)
                val android10Features = mockApi.getAndroidVersionFeatures(29)

                val versionsFeaturesList = listOf(oreoFeatures, pieFeatures, android10Features)
                uiState.value = UiState.Success(versionsFeaturesList)
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network request failed.")
            }
        }
    }

    fun performNetworkRequestsConcurrently() = runBlocking {
        uiState.value = UiState.Loading

        val oreoFeatures = viewModelScope.async { mockApi.getAndroidVersionFeatures(27) }
        val pieFeatures = viewModelScope.async { mockApi.getAndroidVersionFeatures(28) }
        val android10Features = viewModelScope.async { mockApi.getAndroidVersionFeatures(29) }

        viewModelScope.launch {
            try {
                val versionFeaturesList = awaitAll(oreoFeatures, pieFeatures, android10Features)
                uiState.value = UiState.Success(versionFeaturesList)
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network request failed.")
            }
        }
    }
}
