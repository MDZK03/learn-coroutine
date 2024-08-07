package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.*
import timber.log.Timber

class ExceptionHandlingViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun handleWithTryCatch() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                getFeatures(27)
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network request failed: $exception")
            }
        }
    }

    fun handleWithCoroutineExceptionHandler() {
        uiState.value = UiState.Loading

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            uiState.value = UiState.Error("Network request failed: $throwable")
        }
        
        viewModelScope.launch(exceptionHandler) { getFeatures(27) }
    }

    fun showResultsEvenIfChildCoroutineFails() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            supervisorScope {
                val versionFeatures = listOf(
                    async { getFeatures(27) },
                    async { getFeatures(28) },
                    async { getFeatures(29) }
                ).mapNotNull {
                    try {
                        it.await()
                    } catch (e: Exception) {
                        if (e is CancellationException) {
                            throw CancellationException()
                        } else {
                            Timber.e("Error loading features.")
                            null
                        }
                    }
                }
                
                uiState.value = UiState.Success(versionFeatures)
            }
        }
    }

    private suspend fun getFeatures(apiLevel: Int): VersionFeatures {
        return api.getAndroidVersionFeatures(apiLevel)
    }
}
