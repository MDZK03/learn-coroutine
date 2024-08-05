package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase10

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CalculationInBackgroundViewModel : BaseViewModel<UiState>() {

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading
        viewModelScope.launch {

            Timber.d("Coroutine Context: $coroutineContext")
            var result: BigInteger
            val computationDuration = measureTimeMillis {
                result = calculateFactorial(factorialOf)
            }

            var resultString: String
            val stringConvertDuration = measureTimeMillis {
                resultString = withContext(Dispatchers.Default) {
                    result.toString()
                }
            }
            uiState.value =
                UiState.Success(resultString, computationDuration, stringConvertDuration)
        }
    }

    private suspend fun calculateFactorial (number: Int): BigInteger  = withContext(Dispatchers.Default)
    {
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        }
        factorial
    }
}
