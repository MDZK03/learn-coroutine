package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

class NetworkRequestWithTimeoutViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest(timeout: Long) {
        uiState.value = UiState.Loading //show loading

        // usingTimeout(timeout)
        usingTimeoutOrNull(timeout)
    }

    private fun usingTimeout(timeout: Long) {
        viewModelScope.launch {
            try {
                val recentAndroidVersions = withTimeout(timeout) {
                    api.getRecentAndroidVersions()
                }
                uiState.value = UiState.Success(recentAndroidVersions) //show result
            } catch (timeoutCancelException: TimeoutCancellationException) {
                uiState.value = UiState.Error("Network request timed out.")
            } catch (exception: Exception) {
                Timber.e(exception) // log the exception
                uiState.value = UiState.Error("Network request failed.") //toast msg to show error
            }
        }
    }
    private fun usingTimeoutOrNull(timeout: Long) {
        viewModelScope.launch {
            try {
                val recentAndroidVersions = withTimeoutOrNull(timeout) {
                    api.getRecentAndroidVersions()
                }
                if (recentAndroidVersions != null) {
                    uiState.value = UiState.Success(recentAndroidVersions) //show result
                } else uiState.value = UiState.Error("Network request timed out.")
            } catch (exception: Exception) {
                Timber.e(exception) // log the exception
                uiState.value = UiState.Error("Network request failed.") //toast msg to show error
            }
        }
    }

}
