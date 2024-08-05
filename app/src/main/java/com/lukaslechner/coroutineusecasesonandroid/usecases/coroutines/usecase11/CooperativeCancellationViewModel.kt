package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CooperativeCancellationViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private var cancellableJob: Job? = null
    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading

        cancellableJob = viewModelScope.launch {
            try {
                Timber.d("Coroutine Context: $coroutineContext")
                var result: BigInteger
                val computationDuration = measureTimeMillis {
                    result = calculateFactorial(factorialOf)
                }

                var resultString: String
                val stringConvertDuration = measureTimeMillis {
                    resultString = withContext(defaultDispatcher) {
                        result.toString()
                    }
                }
                uiState.value =
                    UiState.Success(resultString, computationDuration, stringConvertDuration)
            } catch (exception: Exception) {
                if (exception is CancellationException) {
                    uiState.value = UiState.Error("Calculation cancelled.")
                } else uiState.value = UiState.Error("Error while calculating.")
            }
        }
    }

    private suspend fun calculateFactorial (number: Int): BigInteger  = withContext(defaultDispatcher) {
        yield()
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        }
        factorial
    }

    fun cancelCalculation() {
        cancellableJob?.cancel()
    }

    fun uiState(): LiveData<UiState> = uiState

    private val uiState: MutableLiveData<UiState> = MutableLiveData()
}
