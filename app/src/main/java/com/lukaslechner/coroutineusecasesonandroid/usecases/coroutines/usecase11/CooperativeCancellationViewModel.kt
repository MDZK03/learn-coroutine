package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase11

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
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
) : BaseViewModel<UiState>() {

    private var cancellableJob: Job? = null
    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading

        cancellableJob = viewModelScope.launch {
            try {
                Timber.d("Coroutine Context: $coroutineContext")
                var result: BigInteger
                val computationDuration = measureTimeMillis { result = calculateFactorial(factorialOf) }

                var resultString: String
                val stringConvertDuration = measureTimeMillis {
                    resultString = withContext(defaultDispatcher) { result.toString() }
                }
                uiState.value = UiState.Success(resultString, computationDuration, stringConvertDuration)
            } catch (e: Exception) {
                if (e is CancellationException) {
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

    fun cancelCalculation() { cancellableJob?.cancel() }
}
