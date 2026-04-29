package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class CalculatorState (
    val display: String = "0"
)

sealed class CalculatorEvent {
    data class Number(val number: String): CalculatorEvent ()
    data class Operator(val operator: String): CalculatorEvent ()
    object Delete: CalculatorEvent ()
    object AllClear: CalculatorEvent ()
    object Calculate: CalculatorEvent ()
    object Decimal: CalculatorEvent ()
}

class CalculatorViewModel: ViewModel() {
    private var num1: String = ""
    private var num2: String = ""
    private var operator: String? = null

    var state by mutableStateOf(CalculatorState ())
        private set

    fun onEvent (event: CalculatorEvent){
        when(event) {
            is CalculatorEvent.Number -> enterNumber( number = event.number)
            is CalculatorEvent.Operator -> enterOperator( operator = event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Delete -> clearLast()
            is CalculatorEvent.Calculate -> performCalculation()
        }
    }

    private fun enterNumber (number: String) {
        if (operator == null) {
            num1 += number
            state = state.copy(display = num1)
        } else {
            num2 += number
            state = state.copy(display = num2)
        }
    }

    private fun enterDecimal () {
        val currentNumber = if (operator == null) num1 else num2
        if(!currentNumber.contains (other = ".")) {
            if (operator == null) {
                num1 += "."
                state = state.copy(display = num1)
            } else {
                num2 += "."
                state = state.copy(display = num2)
            }
        }
    }

    private fun enterOperator (operator: String) {
        if (num1.isBlank()) return

        if (num2.isNotBlank() && this.operator != null) {
            performCalculation()
        }

        this.operator = operator
    }

    private fun clearAll () {
        num1 = ""
        num2 = ""
        operator = null
        state = state.copy(display = "0")
    }

    private fun clearLast () {
        if (operator == null) {
            num1 = num1.dropLast(n = 1)
            state = state.copy(display = num1)
        } else {
            num2 = num2.dropLast(n = 1)
            state = state.copy(display = num2)
        }
    }

    private fun performCalculation () {
        val number1 = num1.toDoubleOrNull()
        val number2 = num2.toDoubleOrNull()
        if (number1 != null && number2 != null && operator != null) {
            val result = when(operator) {
                "+" -> number1 + number2
                "−" -> number1 - number2
                "×" -> number1 * number2
                "÷" -> if (number2 != 0.0) number1 / number2 else Double.NaN
                else -> 0.0
            }
            clearAll()
            val resultStr = if (result.isNaN()) "ERROR" else result.toString()
                .removeSuffix(suffix = ".0")
            num1 = if(!result.isNaN()) resultStr else ""
            state = state.copy(display = resultStr)
        }
    }
}