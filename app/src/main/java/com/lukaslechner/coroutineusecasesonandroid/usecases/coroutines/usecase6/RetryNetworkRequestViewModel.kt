package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RetryNetworkRequestViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading //show loading

        viewModelScope.launch {
            val numberOfRetries = 2
            try {
                retry(numberOfRetries) {
                    loadAndroidVersion()
                }
                /* repeat(numberOfRetries) {
                    try {
                        loadAndroidVersion()
                        return@launch
                    } catch (exception: Exception) {
                        Timber.e(exception) // log the exception
                    }
                }
                loadAndroidVersion() */
            } catch (exception: Exception) {
                Timber.e(exception) // log the exception
                uiState.value = UiState.Error("Network request failed.") //toast msg to show error
            }
        }
    }

    private suspend fun <T> retry(
        numOfRetries: Int,
        initialDelayMilisec: Long = 100,
        maxDelayMilisec: Long = 3000,
        factor: Double = 2.5, //after each retry attempt, * factor
        block: suspend ()-> T): T {
        var currentDelay = initialDelayMilisec
        repeat(numOfRetries) {
            try {
                return block()
            } catch (exception: Exception) {
                Timber.e(exception)
            }
            delay(currentDelay)
            currentDelay = (currentDelay*factor).toLong().coerceAtMost(maxDelayMilisec)
        }
        return block()
    }

    private suspend fun loadAndroidVersion() {
        val recentAndroidVersions = api.getRecentAndroidVersions()
        uiState.value = UiState.Success(recentAndroidVersions) //show result
    }

}
