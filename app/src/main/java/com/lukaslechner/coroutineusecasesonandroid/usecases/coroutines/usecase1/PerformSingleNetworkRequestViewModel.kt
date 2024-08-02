package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi

class PerformSingleNetworkRequestViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performSingleNetworkRequest() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            val recentAndroidVersions = mockApi.getRecentAndroidVersions()
            uiState.value = UiState.Success(recentAndroidVersions)
        }
    }
}
